package com.taitl.existential.configs;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.indexes.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.invariants.*;
import com.taitl.existential.intents.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Implements constraints and effects at the transaction level.
 *
 * "Transactions" in this library are not database transactions. When integrating
 * with a database, they typically align with it, but they are markers of the
 * beginning and end of a business operation (web request, batch job, etc.).
 *
 * The transaction end (a commit) is where expressions such as All and Exists,
 * defined in the current Context and its parent Contexts, are evaluated.
 *
 * A Transaction instance can store data to pass between event handlers.
 *
 * TransactionIndexes
 * When we encounter events such as object creation or mutation, we may need to
 * store derived data. For example, an index can be created so that an Exists<>
 * expression evaluates efficiently: {@code On<Cat>((c, tr) -> tr.index("location_to_cats").put(c.location, c))}
 *
 * TransactionEvents
 * Different contexts may be interested in different event types. To speed up
 * the "which events should be emitted for this context?" lookup, the set of
 * relevant event types (from the context and all parents) is created at
 * transaction start and stored in the Transaction.
 *
 * Customizing
 * To use a custom Transaction class, define them at different context levels
 * and ask the system to provide the appropriate instance using
 * {@code Context.transaction()}.
 * For example, for operation "/app/orders/update":
 * <pre>{@code
 * Ex.contexts().get("/app").transaction(() -> new AppTransaction());
 * Ex.contexts().get("/app/orders").transaction(() -> new OrdersTransaction());
 * Ex.contexts().get("/app/orders/update").transaction(() -> new OrdersUpdateTransaction());
 * }</pre>
 * If a custom transaction class is not defined for a context, the class from
 * its parent context is used.
 *
 * Custom Transaction instance
 * When initiating a business transaction (e.g. with Ex.begin()), you can
 * specify a custom Transaction instance. This is helpful when you need to
 * parameterize rules and expressions based on immediate circumstances such as
 * enclosing method arguments or other dynamic data.
 * To do so, instead of calling Ex.begin(opName), call Ex.begin(opName, transaction)
 * with the custom Transaction instance.
 *
 * @see Context
 * @see TransactionIndexes
 */
// TODO: Delegate to ConcreteTransaction
public class Transaction implements Configurable, Evaluable
{
    public static Supplier<? extends Transaction> FACTORY = () -> Creator.create(Transaction.class);

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
    // public Instructions instructions = new Instructions();

    TransactionIndexes indexes = new TransactionIndexes(this);

    /**
     * Creates a transaction for the specified operation and name.
     *
     * @param op operation name
     * @param name transaction name
     */
    public Transaction(String op, String name)
    {
        sane(op, "op", name, "name");
        this.op = op;
        this.name = name;
        this.id = generateId();
    }

    /**
     * Returns a transaction-local index by name, creating it when needed.
     *
     * @param name index name
     * @return index instance
     */
    public <K, V> Index<K, V> index(String name)
    {
        sane(name, "name");
        return indexes.getOrCreate(name);
    }

    /*
     * Configure invariants, effects, lifecycle rules
     */

    /**
     * Sets invariants (rules) enforced on this transaction's business operation (Context).
     *
     * <pre>{@code
     * Ex.contexts().get("/app/flight_school")
     *     .transaction(() -> new Transaction() {{
     *         invariant(new Invariant<Pilot>() {{
     *             all((p0, p1) -> p1.hours >= p0.hours, "Flight hours cannot go down");
     *             transit((p0, p1) -> p0.flying && !p1.flying, p1.hours += p1.flight().hours);
     *         }});
     *     }});
     * }</pre>
     *
     * @param <T>       Type parameter
     * @param invariant Invariant (rules) that must be upheld
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

    /**
     * Registers an effect collection for this transaction.
     *
     * @param effect effect definition to add
     * @param <T> entity type
     */
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

    /**
     * Registers an intent collection for this transaction.
     *
     * @param intent
     *            Intent definition to add
     * @param <T>
     *            Entity type
     */
    public <T> void intent(Intent<T> intent)
    {
        sane(intent, "intent");
        Transaction tr = intent.transaction();
        if (tr == null)
        {
            intent.transaction(this);
        }
        else
        {
            check(tr == this, "Argument 'intent' must belong to same transaction");
        }
        add(intent);
    }

    /**
     * Registers a lifecycle rule collection for this transaction.
     *
     * @param cycle lifecycle rule set
     * @param <T> transaction subtype
     */
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
     * Ex.contexts().get("/app/flight_school/pilots/update")
     *     .transaction(() -> new Transaction() {
     *         Pilot curPilot;
     *         {
     *             begin(params -> curPilot = (Pilot) params.get("pilot"));
     *             access(...);
     *             invariant(...);
     *             intent(...);
     *         }
     *     });
     * }</pre>
     */
    public <T extends Transaction> void begin(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>(transactionTypeKey()) {
            {
                begin(action);
            }
        });
    }

    /**
     * Adds a commit handler to the transaction lifecycle.
     *
     * @param action commit action to run
     * @param <T> transaction subtype
     */
    public <T extends Transaction> void commit(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>(transactionTypeKey()) {
            {
                commit(action);
            }
        });
    }

    /**
     * Adds a rollback handler to the transaction lifecycle.
     *
     * @param action rollback action to run
     * @param <T> transaction subtype
     */
    public <T extends Transaction> void rollback(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>(transactionTypeKey()) {
            {
                rollback(action);
            }
        });
    }

    /**
     * Adds a checkpoint handler to the transaction lifecycle.
     *
     * @param action checkpoint action to run
     * @param <T> transaction subtype
     */
    public <T extends Transaction> void checkpoint(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>(transactionTypeKey()) {
            {
                checkpoint(action);
            }
        });
    }

    /*
     * Configurable interface
     */

    /**
     * Adds a collection of handlers or expressions to this transaction.
     *
     * @param evs handlers or expressions to add
     * @param <T> entity type
     */
    public <T> void add(Evs<T> evs)
    {
        sane(evs, "evs");
        this.evs.add(evs);
        // instructions.addAll(evs);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Transaction> com.taitl.existential.keys.TypeKey<T> transactionTypeKey()
    {
        return (com.taitl.existential.keys.TypeKey<T>) new com.taitl.existential.keys.TypeKey<Transaction>(
                Transaction.class);
    }

    /**
     * Returns all configured event/value specifications for this transaction.
     *
     * @return list of specifications
     */
    public List<Evs<?>> evs()
    {
        return evs;
    }

    /*
     * Attributes
     */

    /**
     * Sets the operation name for this transaction.
     *
     * @param op operation name
     */
    public void op(String op)
    {
        this.op = op;
    }

    /**
     * Sets a human-friendly name for the transaction.
     *
     * @param name transaction name
     */
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

    /**
     * Associates this transaction with a context instance.
     *
     * @param context context for rule evaluation
     */
    public void context(Context context)
    {
        sane(context, "context");
        this.context = context;
    }

    /**
     * Verifies that the transaction has an assigned context.
     */
    public void validate()
    {
        verify(context != null, "Transaction context is not set, call context(str) first");
    }
}
