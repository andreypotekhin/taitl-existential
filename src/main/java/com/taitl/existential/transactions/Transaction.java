package com.taitl.existential.transactions;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.core.instructions.*;
import com.taitl.ex.core.transactions.*;
import com.taitl.ex.logic.events.logic.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.invariants.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Implements constraints and effects on a transaction level.
 *
 * 'Transactions' in this library are not related to database transactions, although when integrating, they assumed
 * to be aligned with ones (if you have such). They are simply markers of beginning and end of processing - of a
 * business transaction, web request etc.
 *
 * Transaction's end (a commit) serves as the point where the expressions, such as All and Exists, defined in
 * the current Context as well as its parent Contexts, are evaluated.
 *
 * Transaction object can store data for passing from one event handler to another.
 *
 * TransactionIndexes
 * When we encounter various events, such as objects creation or mutation, we might need to store some data about it.
 * For instance, we may wish to create an index on entities, so that Exists<> expression could be evaluated in a
 * performant way: {@code On<Cat>((c, tr) -> tr.index("location_to_cats").put(c.location, c))}
 *
 * TransactionEvents
 * Different contexts may be interested in different types of events. To speed up the answer to question 'which events
 * should be emitted for this context?', the set of relevant event types (from the context as well as all its parents)
 * is created at transaction start and stored in the Transaction object.
 *
 * Customizing
 * To use a custom class for transaction, define them in different context levels, and ask the system to provide
 * an appropriate instance for a business operation using {@code Context.transaction()}.<br>
 * For instance, for operation "/app/orders/update":<br>
 * For data relevant to all transactions, define a custom {@code AppTransaction } class.<br>
 * An {@code OrdersTransaction} class extending {@code AppTransaction } can be used for the transactions related to
 * Orders module. If further customization needed, you could also define {@code OrdersUpdateTransaction} class extending
 * {@code OrdersTransaction}, and so on.<br>
 * Setting up context-customized Transaction classes:<br>
 * <pre>{@code
 *   Ex.contexts().get("/app").transaction(() -> new AppTransaction())
 *   Ex.contexts().get("/app/orders").transaction(() -> new OrdersTransaction())
 *   Ex.contexts().get("/app/orders/update").transaction(() -> new OrdersUpdateTransaction())
 *   }</pre>
 * If custom transaction class is not defined for a context, the transaction class from its parent context is used.
 *
 * Custom Transaction instance
 * When initiating a business transaction (e.g. with Ex.begin()), you can specify a custom Transaction instance for it.
 * This is helpful when you need to parameterize rules and expressions based on immediate circumstances such as
 * enclosing method arguments, or other dynamic circumstances.
 * To do so, instead of calling Ex.begin(opName), call Ex.begin(opName, transaction) with custom Transaction instance.
 *
 * @see Context
 * @see TransactionIndexes
 */
// TODO: Delegate to ConcreteTransaction
public class Transaction implements Configurable, Evaluable
{
    public final UUID id;
    public String op;
    public String name;
    public Context context;

    // TODO: split by stage (execution, validation)
    List<Evs<?>> evs = new ArrayList<>();

    /**
     * Instructions - event handlers. Includes all event handlers (rules)
     * defined in this context.
     */
    public Instructions instructions = new Instructions();

    TransactionIndexes indexes = new TransactionIndexes(this);

    public Transaction(String op, String name)
    {
        sane(op, "op", name, "name");
        this.op = op;
        this.name = name;
        this.id = generateId();
    }

    public <K, V> Index<K, V> index(String name)
    {
        sane(name, "name");
        return indexes.get(name);
    }

    /*
     * Configure invariants, effects, lifecycle rules
     */

    /**
     * Set up invariants/rules to be enforced on this transaction's business operation (Context).
     *
     * <pre>{@code
     *    Ex.contexts().get("/app/flight_school")
     *     .transaction(() -> new Transaction(){{
     * 	      invariant(new Invariant<Pilot>() {{
     *                all((p0, p1) -> p1.hours >= p0.hours, "Flight hours can not go down");
     *                transit((p0, p1) -> p0.flying && !p1.flying, p1.hours += p1.flight().hours);
     *          }})
     * }</pre>
     *
     * @param <T>        Type parameter
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
        add(invariant);
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
        add(effect);
    }

    public <T extends Transaction> void cycle(Trancycle<T> cycle)
    {
        sane(cycle, "cycle");
        Transaction tr = cycle.transaction();
        if (tr == null)
        {
            cycle.transaction(this);
        }
        else
        {
            check(tr == this, "Argument 'cycle' must belong to same transaction");
        }
        add(cycle);
    }

    /*
     * Convenience methods for lifecycle
     */

    /**
     * Add OnBegin<Transaction> handler.
     * Example:
     * Declare transaction member (curPilot) and initialize it at the start of transaction.
     * <pre>{@code
     *    Ex.contexts().get("/app/flight_school/pilots/update")
     *        .transaction(() -> new Transaction(){
     *          Pilot curPilot;
     *            {
     * 			    begin(params -> curPilot = (Pilot)params.get("pilot"));
     * 			    access(...);
     * 			    invariant(...);
     * 			    intent(...);
     *            }});
     * }</pre>
     */
    public <T extends Transaction> void begin(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>() {
            {
                begin(action);
            }
        });
    }

    public <T extends Transaction> void commit(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>() {
            {
                commit(action);
            }
        });
    }

    public <T extends Transaction> void rollback(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>() {
            {
                rollback(action);
            }
        });
    }

    public <T extends Transaction> void checkpoint(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>() {
            {
                checkpoint(action);
            }
        });
    }

    /*
     * Configurable interface
     */

    public <T> void add(Evs<T> evs)
    {
        sane(evs, "evs");
        this.evs.add(evs);
        instructions.addAll(evs);
    }

    public List<Evs<?>> evs()
    {
        return evs;
    }

    /**
     * TODO: allow(Intent<T> intent) { ...
     * intent.tran = this; ... }
     */

    /*
     * Attributes
     */

    public void op(String op)
    {
        this.op = op;
    }

    public void name(String name)
    {
        this.name = name;
    }

    protected UUID generateId()
    {
        return UUID.randomUUID();
    }

    public Context context()
    {
        cool(context, "context");
        return context;
    }

    public void context(Context context)
    {
        sane(context, "context");
        this.context = context;
    }

    public void validate()
    {
        verify(context != null, "Transaction context is not set, call context(str) first");
    }
}
