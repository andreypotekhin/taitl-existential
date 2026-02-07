package com.taitl.existential.transactions;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.core.events.*;
import com.taitl.ex.core.instructions.*;
import com.taitl.ex.core.transactions.*;
import com.taitl.ex.logic.unused.indexes.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.expressions.*;
import com.taitl.existential.handlers.transaction_handlers.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.invariants.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Implements a transaction object - a unit of processing within a context.
 * <p>
 * 'Transactions' in this library are not related to database transactions, although when integrating, they assumed
 * to be aligned with ones (if you have such). They are simply markers of beginning and end of processing - of a
 * business transaction, web request etc.
 * <p>
 * Transaction's end (a commit) serves as the point where we evaluate expressions, such as All and Exists, defined in
 * the current Context as well as its parent Contexts.
 * <p>
 * Transaction object can store data for passing from one event handler to another.
 * <p>
 * TransactionIndexes<p>
 *   When we encounter various events, such as objects creation or mutation, we might need to store some data about it.
 *   For instance, we may wish to create an index on entities, so that Exists<> expression could be evaluated in a
 *   performant way: {@code On<Cat>((c, tr) -> tr.index("location_to_cats").put(c.location, c))}
 * <p>
 * TransactionEvents<p>
 *   Different contexts may be interested in different types of events. To speed up the answer to question 'which events
 *   should be emitted by EventSplitter for this context?', the set of relevant event types (from the context as well as
 *   all its parents) is created at transaction start and stored in Transaction object. This allows to avoid having to
 *   gather such information for each event from each involved context.
 * <p>
 * Customizing<p>
 *   To use custom classes for transactions, define them in different context levels, and ask the system to provide
 *   an appropriate instance for a business operation using {@code Context.transactionFactory()}.<br>
 *   For instance, for operation "/app/orders/update":<br>
 *   For data relevant to all transactions, define a custom {@code AppTransaction } class.<br>
 *   An {@code OrdersTransaction} class extending {@code AppTransaction } can be used for the transactions related to
 *   Orders module.If further customization needed, you could also define {@code OrdersUpdateTransaction} class extending
 *   {@code OrdersTransaction}, and so on.<br>
 *   Setting up context-customized Transaction classes:<br>
 *   <pre>{@code
 *   Ex.contexts().get("/app").transactionFactory(() -> new AppTransaction())
 *   Ex.contexts().get("/app/orders").transactionFactory(() -> new OrdersTransaction())
 *   Ex.contexts().get("/app/orders/update").transactionFactory(() -> new OrdersUpdateTransaction())
 *   }</pre>
 *   If custom transaction class is not defined for a context, the transaction class from its parent context is used.
 *
 * @see Context
 * @see TransactionIndexes
 * @see TransactionEvents
 * @see EventSplitter
 */
public class Transaction implements Configurable
{
    public final UUID id;
    public String op;
    public Context context;

    /**
     * Instructions - event handlers. Includes all event handlers (rules)
     * defined in this context.
     */
    public Instructions instructions = new Instructions();

    /**
     * Expressions, such as All<T>, defined in this context.
     */
    public Expressions expressions = new Expressions();

    TransactionIndexes indexes = new TransactionIndexes(this);
    TransactionEvents events = new TransactionEvents(this);

    public Transaction(String op)
    {
        if (op == null)
        {
            throw new IllegalArgumentException(Strings.ARG_OP);
        }
        this.op = op;
        this.id = generateId();
    }

    public <K, V> Index<K, V> index(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException(Strings.ARG_NAME);
        }
        return indexes.get(name);
    }

    protected UUID generateId()
    {
        return UUID.randomUUID();
    }

    public Context getContext()
    {
        cool(context, "context");
        return context;
    }

    public void setContext(Context context)
    {
        sane(context, "context");
        this.context = context;
    }

    public <T> Transaction add(EventHandler<T> eh)
    {
        sane(eh, "eh");
        instructions.add(eh);
        return this;
    }

    public <T> Transaction add(Expression<T> expr)
    {
        sane(expr, "expr");
        expressions.add(expr);
        return this;
    }

    /* Transaction-related methods */

    /**
     * Add OnBegin<Transaction> handler.
     *
     * Example:
     * Declare transaction member (curPilot) and initialize it at the start of transaction:
     * <pre>{@code
     *    Ex.contexts().get("/app/flight_school/pilots/update")
     *        .transaction(() -> new Transaction(){
     *          Pilot curPilot;
     * 		 	{
     * 			    begin(params -> curPilot = (Pilot)params.get("pilot"))
     * 			    require(...);
     * 			    intent(...);
     * 			}});
     * }</pre>
     *
     * @param action
     * @return This object
     */
    public Transaction begin(Consumer<? super Transaction> action)
    {
        sane(action, "action");
        return add(new OnBegin<Transaction>(action));
    }

    /**
     * Set up invariants/rules to be enforced on this transaction's business operation (Context).
     *
     * <pre>{@code
     *    Ex.contexts().get("/app/flight_school")
     *     .transaction(() -> new Transaction(){{
     * 	      invariant(new Invariant<Pilot>() {{
     *                all((p0, p1) -> p1.hours >= p0.hours, "Flight hours can not go down");
     *                transit((p0, p1) -> p0.flying && !p1.flying, p1.hours += p1.flight().hours);
     * 	      }})
     * }</pre>
     *
     * @param <T> Type parameter
     * @param invariants Invariants (rules) that must be upheld
     */
    public <T> void invariant(Invariant<T> invariant)
    {
        sane(invariant, "invariant");
        Transaction tr = invariant.transaction();

        if (tr == null)
        {
            invariant.transaction(this);
        }
        else
        {
            check(tr == this, "Argument 'invariant' must belong to same transaction");
        }

        instructions.addAll(invariant.instructions);
        expressions.addAll(invariant.expressions);
    }

    public <T> void effect(Effect<T> effect)
    {
        sane(effect, "effect");
        Transaction tr = effect.getTransaction();

        if (tr == null)
        {
            effect.setTransaction(this);
        }
        else
        {
            check(tr == this, "Argument 'effect' must belong to same transaction");
        }

        instructions.addAll(effect.instructions);
        expressions.addAll(effect.expressions);
    }

    /**
     * TODO: allow(Intent<T> intent) { ...
     * intent.tran = this; ... }
     */

    public void name(String name)
    {
        this.op = name;
    }
}
