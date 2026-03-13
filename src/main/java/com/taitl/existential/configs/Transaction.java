package com.taitl.existential.configs;

import com.taitl.ex.core.indexes.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.transaction_handlers.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.interfaces.*;

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
 * expression evaluates efficiently:
 * {@code On<Cat>((c, tr) -> tr.index("location_to_cats", cat -> cat.location).add(c))}
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
    public static BiFunction<String, String, ? extends Transaction> FACTORY = Transaction::new;
    protected static final Map<Class<?>, StageName> LIFECYCLE_STAGE_BY_HANDLER = lifecycleStageByHandler();

    public final UUID id;
    public String op;
    public String name;
    public Context context;

    /**
     * Configured rules partitioned by execution stage.
     */
    protected Stage stage = new Stage();
    protected StageName stageCursor;

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
    public <K, V> MultiIndex<K, V> index(String name, Function<V, K> getKey)
    {
        sane(name, "name", getKey, "getKey");
        return indexes.getOrCreate(name, getKey);
    }

    /*
     * Configure invariants, effects, lifecycle rules
     */

    /**
     * Sets invariants (rules) enforced on this transaction's business operation (Context).
     *
     * <pre>{@code
     * Ex.contexts().get("/app/flight_school")
     *     .transaction(() -> new Transaction("/app/flight_school", "flight-school") {{
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
        invariant(invariant, resolvedStage(invariant));
    }

    public <T> void invariant(Invariant<T> invariant, StageName stageName)
    {
        sane(invariant, "invariant");
        if (!invariant.hasTransaction())
        {
            invariant.transaction(this);
        }
        else
        {
            Transaction tr = invariant.transaction();
            check(tr == this, "Argument 'invariant' must belong to this transaction. " +
                    "Create it here or call invariant.transaction(this).");
        }
        add(invariant, stageName);
    }

    /**
     * Registers an effect collection for this transaction.
     *
     * @param effect effect definition to add
     * @param <T> entity type
     */
    public <T> void effect(Effect<T> effect)
    {
        effect(effect, resolvedStage(effect));
    }

    public <T> void effect(Effect<T> effect, StageName stageName)
    {
        sane(effect, "effect");
        if (!effect.hasTransaction())
        {
            effect.setTransaction(this);
        }
        else
        {
            Transaction tr = effect.getTransaction();
            check(tr == this, "Argument 'effect' must belong to this transaction. " +
                    "Create it here or call effect.setTransaction(this).");
        }
        add(effect, stageName);
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
        intent(intent, resolvedStage(intent));
    }

    public <T> void intent(Intent<T> intent, StageName stageName)
    {
        sane(intent, "intent");
        if (!intent.hasTransaction())
        {
            intent.transaction(this);
        }
        else
        {
            Transaction tr = intent.transaction();
            check(tr == this, "Argument 'intent' must belong to this transaction. " +
                    "Create it here or call intent.transaction(this).");
        }
        add(intent, stageName);
    }

    /**
     * Registers a lifecycle rule collection for this transaction.
     *
     * @param cycle lifecycle rule set
     * @param <T> transaction subtype
     */
    public <T extends Transaction> void cycle(Life<T> cycle)
    {
        sane(cycle, "cycle");
        if (stageCursor != null)
        {
            cycle(cycle, stageCursor);
            return;
        }

        EnumSet<StageName> lifecycleStages = lifecycleStages(cycle);
        for (StageName stageName : lifecycleStages)
        {
            cycle(cycle, stageName);
        }
    }

    public <T extends Transaction> void cycle(Life<T> cycle, StageName stageName)
    {
        sane(cycle, "cycle");
        if (!cycle.hasTransaction())
        {
            cycle.transaction(this);
        }
        else
        {
            Transaction tr = cycle.transaction();
            check(tr == this, "Argument 'cycle' must belong to same transaction");
        }
        add(cycle, stageName);
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
     *     .transaction(() -> new Transaction("/app/flight_school/pilots/update", "pilot-update") {
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
        addLifecycle(action, StageName.BEGIN, cycle -> cycle.begin(action));
    }

    /**
     * Adds a commit handler to the transaction lifecycle.
     *
     * @param action commit action to run
     * @param <T> transaction subtype
     */
    public <T extends Transaction> void commit(Consumer<? super T> action)
    {
        addLifecycle(action, StageName.COMMIT, cycle -> cycle.commit(action));
    }

    /**
     * Adds a rollback handler to the transaction lifecycle.
     *
     * @param action rollback action to run
     * @param <T> transaction subtype
     */
    public <T extends Transaction> void rollback(Consumer<? super T> action)
    {
        addLifecycle(action, StageName.ROLLBACK, cycle -> cycle.rollback(action));
    }

    /**
     * Adds a checkpoint handler to the transaction lifecycle.
     *
     * @param action checkpoint action to run
     * @param <T> transaction subtype
     */
    public <T extends Transaction> void checkpoint(Consumer<? super T> action)
    {
        addLifecycle(action, StageName.CHECKPOINT, cycle -> cycle.checkpoint(action));
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
        add(evs, resolvedStage(evs));
    }

    public <T> void add(Evs<T> evs, StageName stageName)
    {
        sane(evs, "evs", stageName, "stageName");
        stage.add(stageName, evs);
    }

    public Transaction begin()
    {
        stageCursor = StageName.BEGIN;
        return this;
    }

    public Transaction immediate()
    {
        stageCursor = StageName.IMMEDIATE;
        return this;
    }

    public Transaction validation()
    {
        stageCursor = StageName.VALIDATION;
        return this;
    }

    public Transaction commit()
    {
        stageCursor = StageName.COMMIT;
        return this;
    }

    public Transaction checkpoint()
    {
        stageCursor = StageName.CHECKPOINT;
        return this;
    }

    public Transaction rollback()
    {
        stageCursor = StageName.ROLLBACK;
        return this;
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
        return stage.all();
    }

    public Stage stage()
    {
        return stage;
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

    protected <T> StageName resolvedStage(Evs<T> evs)
    {
        sane(evs, "evs");
        if (stageCursor != null)
        {
            return stageCursor;
        }
        return defaultStage(evs);
    }

    protected <T> StageName defaultStage(Evs<T> evs)
    {
        sane(evs, "evs");
        if (evs instanceof Intent<?>)
        {
            return StageName.IMMEDIATE;
        }
        if (evs instanceof Life<?>)
        {
            return StageName.BEGIN;
        }
        return StageName.VALIDATION;
    }

    protected StageName lifecycleStage(EventHandler<?> handler)
    {
        sane(handler, "handler");
        StageName stageName = LIFECYCLE_STAGE_BY_HANDLER.get(handler.getClass());
        if (stageName != null)
        {
            return stageName;
        }
        for (Map.Entry<Class<?>, StageName> entry : LIFECYCLE_STAGE_BY_HANDLER.entrySet())
        {
            if (entry.getKey().isAssignableFrom(handler.getClass()))
            {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("Unsupported lifecycle handler type: " + handler.getClass().getName());
    }

    protected <T extends Transaction> EnumSet<StageName> lifecycleStages(Life<T> cycle)
    {
        sane(cycle, "cycle");
        EnumSet<StageName> stages = EnumSet.noneOf(StageName.class);
        for (Ev<T> ev : cycle.list())
        {
            if (!(ev instanceof EventHandler<?>))
            {
                throw new IllegalArgumentException(
                        "Lifecycle rule must be an EventHandler: " + ev.getClass().getName());
            }
            stages.add(lifecycleStage((EventHandler<?>) ev));
        }
        if (stages.isEmpty())
        {
            throw new IllegalArgumentException("Lifecycle rule set must contain at least one handler");
        }
        return stages;
    }

    protected <T extends Transaction> void addLifecycle(
            Consumer<? super T> action,
            StageName stageName,
            Consumer<Life<T>> registrar)
    {
        sane(action, "action", stageName, "stageName", registrar, "registrar");
        Life<T> cycle = new Life<>(transactionTypeKey());
        registrar.accept(cycle);
        cycle(cycle, stageName);
    }

    protected static Map<Class<?>, StageName> lifecycleStageByHandler()
    {
        Map<Class<?>, StageName> map = new LinkedHashMap<>();
        map.put(OnBegin.class, StageName.BEGIN);
        map.put(OnCommit.class, StageName.COMMIT);
        map.put(OnCheckpoint.class, StageName.CHECKPOINT);
        map.put(OnRollback.class, StageName.ROLLBACK);
        return Collections.unmodifiableMap(map);
    }
}
