package com.taitl.existential.builders;

import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.transaction_handlers.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Configures a {@link Transaction} within a specific context.
 * Collects invariants, effects, intents, and transaction lifecycle handlers.
 */
public class TransactionBuilder
{
    protected static final Map<Class<?>, StageName> LIFECYCLE_STAGE_BY_HANDLER = lifecycleStageByHandler();

    ContextBuilder parent;
    String op;
    List<Supplier<? extends Evs<?>>> evsSuppliers;
    List<StageName> evsStages;
    Supplier<? extends Transaction> transactionFactory;
    StageName stageCursor;

    /**
     * Creates a transaction builder for a concrete operation name.
     *
     * @param parentContext
     *            Parent context builder
     * @param op
     *            Operation name to associate with the transaction
     */
    public TransactionBuilder(ContextBuilder parentContext, String op)
    {
        this.parent = parentContext;
        this.op = op;
        this.evsSuppliers = new ArrayList<>();
        this.evsStages = new ArrayList<>();
        this.transactionFactory = parent::createTransactionInstance;
        installParentTransactionFactory();
    }

    /**
     * Creates a transaction builder with a custom transaction factory.
     *
     * @param parentContext
     *            Parent context builder
     * @param transactionFactory
     *            Factory for creating transaction instances
     */
    public TransactionBuilder(ContextBuilder parentContext, Supplier<? extends Transaction> transactionFactory)
    {
        sane(parentContext, "parentContext", transactionFactory, "transactionFactory");
        this.parent = parentContext;
        this.op = null;
        this.evsSuppliers = new ArrayList<>();
        this.evsStages = new ArrayList<>();
        this.transactionFactory = transactionFactory;
        installParentTransactionFactory();
    }

    /**
     * Starts building an {@link Invariant} for a given subject type.
     *
     * @param cls
     *            Subject type for the invariant
     * @param <T>
     *            Subject type for the invariant
     * @return Invariant builder
     */
    public <T> InvariantBuilder<T> invariant(Class<T> cls)
    {
        sane(cls, "cls");
        return invariant(new TypeKey<>(cls));
    }

    /**
     * Starts building an {@link Invariant} for the given type key.
     *
     * @param typeKey
     *            Type key for the invariant subject
     * @param <T>
     *            Subject type for the invariant
     * @return Invariant builder
     */
    public <T> InvariantBuilder<T> invariant(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        InvariantBuilder<T> ib = new InvariantBuilder<>(this, typeKey);
        register(() -> ib.build(), StageName.VALIDATION);
        return ib;
    }

    /**
     * Registers an already-built invariant with this transaction.
     *
     * @param invariant
     *            Invariant to register
     * @param <T>
     *            Subject type for the invariant
     * @return This builder for chaining
     */
    public <T> TransactionBuilder invariant(Invariant<T> invariant)
    {
        sane(invariant, "invariant");
        register(() -> invariant, StageName.VALIDATION);
        return this;
    }

    /**
     * Starts building an {@link Effect} for a given subject type.
     *
     * @param cls
     *            Subject type for the effect
     * @param <T>
     *            Subject type for the effect
     * @return Effect builder
     */
    public <T> EffectBuilder<T> effect(Class<T> cls)
    {
        sane(cls, "cls");
        return effect(new TypeKey<>(cls));
    }

    /**
     * Starts building an {@link Effect} for the given type key.
     *
     * @param typeKey
     *            Type key for the effect subject
     * @param <T>
     *            Subject type for the effect
     * @return Effect builder
     */
    public <T> EffectBuilder<T> effect(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        EffectBuilder<T> eb = new EffectBuilder<>(this, typeKey);
        register(() -> eb.build(), StageName.VALIDATION);
        return eb;
    }

    /**
     * Registers an already-built effect with this transaction.
     *
     * @param effect
     *            Effect to register
     * @param <T>
     *            Subject type for the effect
     * @return This builder for chaining
     */
    public <T> TransactionBuilder effect(Effect<T> effect)
    {
        sane(effect, "effect");
        register(() -> effect, StageName.VALIDATION);
        return this;
    }

    /**
     * Registers a transaction lifecycle rule set.
     *
     * @param cycle
     *            Life to register
     * @param <T>
     *            Transaction type handled by the life
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder cycle(Life<T> cycle)
    {
        sane(cycle, "cycle");
        if (stageCursor != null)
        {
            register(() -> cycle, stageCursor);
            return this;
        }

        EnumSet<StageName> lifecycleStages = lifecycleStages(cycle);
        for (StageName stageName : lifecycleStages)
        {
            register(() -> cycle, stageName);
        }
        return this;
    }

    public <T> IntentBuilder<T> intent(Class<T> cls)
    {
        sane(cls, "cls");
        return intent(new TypeKey<>(cls));
    }

    /**
     * Starts building an {@link Intent} for the given type key.
     *
     * @param typeKey
     *            Type key for the intent subject
     * @param <T>
     *            Subject type for the intent
     * @return Intent builder
     */
    public <T> IntentBuilder<T> intent(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        IntentBuilder<T> ib = new IntentBuilder<>(this, typeKey);
        register(() -> ib.build(), StageName.IMMEDIATE);
        return ib;
    }

    /**
     * Registers an already-built intent with this transaction.
     *
     * @param intent
     *            Intent to register
     * @param <T>
     *            Subject type for the intent
     * @return This builder for chaining
     */
    public <T> TransactionBuilder intent(Intent<T> intent)
    {
        sane(intent, "intent");
        register(() -> intent, StageName.IMMEDIATE);
        return this;
    }

    public ContextBuilder context(String name)
    {
        sane(name, "name");
        return parent.context(name);
    }

    public ContextBuilder context()
    {
        return parent.context();
    }

    public TransactionBuilder begin()
    {
        stageCursor = StageName.BEGIN;
        return this;
    }

    public TransactionBuilder immediate()
    {
        stageCursor = StageName.IMMEDIATE;
        return this;
    }

    public TransactionBuilder validation()
    {
        stageCursor = StageName.VALIDATION;
        return this;
    }

    public TransactionBuilder commit()
    {
        stageCursor = StageName.COMMIT;
        return this;
    }

    public TransactionBuilder checkpoint()
    {
        stageCursor = StageName.CHECKPOINT;
        return this;
    }

    public TransactionBuilder rollback()
    {
        stageCursor = StageName.ROLLBACK;
        return this;
    }

    /**
     * Adds a begin handler to the transaction lifecycle.
     *
     * @param action
     *            Action to invoke on transaction begin
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder begin(Consumer<? super T> action)
    {
        return addLifecycle(action, StageName.BEGIN, cycle -> cycle.begin(action));
    }

    /**
     * Adds a begin handler for the provided transaction type key.
     *
     * @param typeKey
     *            Transaction type key
     * @param action
     *            Action to invoke on transaction begin
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder begin(TypeKey<T> typeKey, Consumer<? super T> action)
    {
        return addLifecycle(typeKey, action, StageName.BEGIN, cycle -> cycle.begin(action));
    }

    /**
     * Adds a begin handler for the provided transaction class.
     *
     * @param typeClass
     *            Transaction class
     * @param action
     *            Action to invoke on transaction begin
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder begin(Class<T> typeClass, Consumer<? super T> action)
    {
        sane(typeClass, "typeClass");
        return begin(new TypeKey<>(typeClass), action);
    }

    /**
     * Adds a commit handler to the transaction lifecycle.
     *
     * @param action
     *            Action to invoke on transaction commit
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder commit(Consumer<? super T> action)
    {
        return addLifecycle(action, StageName.COMMIT, cycle -> cycle.commit(action));
    }

    /**
     * Adds a commit handler for the provided transaction type key.
     *
     * @param typeKey
     *            Transaction type key
     * @param action
     *            Action to invoke on transaction commit
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder commit(TypeKey<T> typeKey, Consumer<? super T> action)
    {
        return addLifecycle(typeKey, action, StageName.COMMIT, cycle -> cycle.commit(action));
    }

    /**
     * Adds a commit handler for the provided transaction class.
     *
     * @param typeClass
     *            Transaction class
     * @param action
     *            Action to invoke on transaction commit
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder commit(Class<T> typeClass, Consumer<? super T> action)
    {
        sane(typeClass, "typeClass");
        return commit(new TypeKey<>(typeClass), action);
    }

    /**
     * Adds a rollback handler to the transaction lifecycle.
     *
     * @param action
     *            Action to invoke on transaction rollback
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder rollback(Consumer<? super T> action)
    {
        return addLifecycle(action, StageName.ROLLBACK, cycle -> cycle.rollback(action));
    }

    /**
     * Adds a rollback handler for the provided transaction type key.
     *
     * @param typeKey
     *            Transaction type key
     * @param action
     *            Action to invoke on transaction rollback
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder rollback(TypeKey<T> typeKey, Consumer<? super T> action)
    {
        return addLifecycle(typeKey, action, StageName.ROLLBACK, cycle -> cycle.rollback(action));
    }

    /**
     * Adds a rollback handler for the provided transaction class.
     *
     * @param typeClass
     *            Transaction class
     * @param action
     *            Action to invoke on transaction rollback
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder rollback(Class<T> typeClass, Consumer<? super T> action)
    {
        sane(typeClass, "typeClass");
        return rollback(new TypeKey<>(typeClass), action);
    }

    /**
     * Adds a checkpoint handler to the transaction lifecycle.
     *
     * @param action
     *            Action to invoke on transaction checkpoint
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder checkpoint(Consumer<? super T> action)
    {
        return addLifecycle(action, StageName.CHECKPOINT, cycle -> cycle.checkpoint(action));
    }

    /**
     * Adds a checkpoint handler for the provided transaction type key.
     *
     * @param typeKey
     *            Transaction type key
     * @param action
     *            Action to invoke on transaction checkpoint
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder checkpoint(TypeKey<T> typeKey, Consumer<? super T> action)
    {
        return addLifecycle(typeKey, action, StageName.CHECKPOINT, cycle -> cycle.checkpoint(action));
    }

    /**
     * Adds a checkpoint handler for the provided transaction class.
     *
     * @param typeClass
     *            Transaction class
     * @param action
     *            Action to invoke on transaction checkpoint
     * @param <T>
     *            Transaction type handled by the action
     * @return This builder for chaining
     */
    public <T extends Transaction> TransactionBuilder checkpoint(Class<T> typeClass, Consumer<? super T> action)
    {
        sane(typeClass, "typeClass");
        return checkpoint(new TypeKey<>(typeClass), action);
    }

    /**
     * Centralizes lifecycle handler wiring to keep the fluent entry points uniform.
     */
    protected <T extends Transaction> TransactionBuilder addLifecycle(
            Consumer<? super T> action,
            StageName stageName,
            Consumer<Life<T>> registrar)
    {
        return addLifecycle(transactionTypeKey(), action, stageName, registrar);
    }

    protected <T extends Transaction> TransactionBuilder addLifecycle(
            TypeKey<T> typeKey,
            Consumer<? super T> action,
            StageName stageName,
            Consumer<Life<T>> registrar)
    {
        sane(typeKey, "typeKey", action, "action", stageName, "stageName");
        Life<T> cycle = new Life<>(typeKey);
        registrar.accept(cycle);
        registerAtStage(() -> cycle, stageName);
        return this;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Transaction> TypeKey<T> transactionTypeKey()
    {
        return (TypeKey<T>) new TypeKey<Transaction>(Transaction.class);
    }

    protected Transaction createInstance()
    {
        return transactionFactory.get();
    }

    protected ContextBuilder contextBuilder()
    {
        return parent;
    }

    protected void register(Supplier<? extends Evs<?>> supplier, StageName defaultStage)
    {
        sane(supplier, "supplier", defaultStage, "defaultStage");
        evsSuppliers.add(supplier);
        evsStages.add(stageCursor != null ? stageCursor : defaultStage);
    }

    protected void registerAtStage(Supplier<? extends Evs<?>> supplier, StageName stageName)
    {
        sane(supplier, "supplier", stageName, "stageName");
        evsSuppliers.add(supplier);
        evsStages.add(stageName);
    }

    protected void installParentTransactionFactory()
    {
        parent.transactionFactory = () -> {
            Transaction tr = createInstance();

            if (op != null)
            {
                tr.op(op);
            }

            for (int i = 0; i < this.evsSuppliers.size(); i++)
            {
                Evs<?> evs = evsSuppliers.get(i).get();
                StageName stageName = evsStages.get(i);
                if (evs instanceof Invariant<?>)
                {
                    tr.invariant((Invariant<?>) evs, stageName);
                }
                else if (evs instanceof Effect<?>)
                {
                    tr.effect((Effect<?>) evs, stageName);
                }
                else if (evs instanceof Intent<?>)
                {
                    tr.intent((Intent<?>) evs, stageName);
                }
                else if (evs instanceof Life<?>)
                {
                    tr.cycle((Life<?>) evs, stageName);
                }
                else
                {
                    throw new IllegalStateException("Unexpected class in ruleSet: " + evs);
                }
            }
            return tr;
        };
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
        throw new IllegalArgumentException(
                "Unsupported lifecycle handler type: " + handler.getClass().getName());
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
