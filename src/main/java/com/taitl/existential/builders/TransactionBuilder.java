package com.taitl.existential.builders;

import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
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
    ContextBuilder parent;
    String op;
    List<Supplier<? extends Evs<?>>> evsSuppliers;
    Supplier<? extends Transaction> transactionFactory;

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
        this.transactionFactory = parent::createTransactionInstance;
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
        this.transactionFactory = transactionFactory;
    }

    /**
     * Finalizes transaction configuration and returns the parent {@link ContextBuilder}.
     *
     * @return Parent builder
     */
    public ContextBuilder build()
    {
        parent.transactionFactory = () -> {
            Transaction tr = createInstance();

            if (op != null)
            {
                tr.op(op);
            }

            for (Supplier<? extends Evs<?>> supplier : this.evsSuppliers)
            {
                Evs<?> evs = supplier.get();
                if (evs instanceof Invariant<?>)
                {
                    tr.invariant((Invariant<?>) evs);
                }
                else if (evs instanceof Effect<?>)
                {
                    tr.effect((Effect<?>) evs);
                }
                else if (evs instanceof Intent<?>)
                {
                    tr.intent((Intent<?>) evs);
                }
                else if (evs instanceof Life<?>)
                {
                    tr.cycle((Life<?>) evs);
                }
                else
                {
                    throw new IllegalStateException("Unexpected class in ruleSet: " + evs);
                }
            }
            return tr;
        };
        return parent;
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
        evsSuppliers.add(() -> ib.build());
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
        evsSuppliers.add(() -> invariant);
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
        evsSuppliers.add(() -> eb.build());
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
        evsSuppliers.add(() -> effect);
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
        evsSuppliers.add(() -> cycle);
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
        evsSuppliers.add(() -> ib.build());
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
        evsSuppliers.add(() -> intent);
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
        return addLifecycle(action, cycle -> cycle.begin(action));
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
        return addLifecycle(typeKey, action, cycle -> cycle.begin(action));
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
        return addLifecycle(action, cycle -> cycle.commit(action));
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
        return addLifecycle(typeKey, action, cycle -> cycle.commit(action));
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
        return addLifecycle(action, cycle -> cycle.rollback(action));
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
        return addLifecycle(typeKey, action, cycle -> cycle.rollback(action));
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
        return addLifecycle(action, cycle -> cycle.checkpoint(action));
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
        return addLifecycle(typeKey, action, cycle -> cycle.checkpoint(action));
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
     * No-op convenience for fluent call sites.
     *
     * @return This builder
     */
    public TransactionBuilder done()
    {
        return this;
    }

    /**
     * Centralizes lifecycle handler wiring to keep the fluent entry points uniform.
     */
    protected <T extends Transaction> TransactionBuilder addLifecycle(Consumer<? super T> action,
            Consumer<Life<T>> registrar)
    {
        return addLifecycle(transactionTypeKey(), action, registrar);
    }

    protected <T extends Transaction> TransactionBuilder addLifecycle(TypeKey<T> typeKey, Consumer<? super T> action,
            Consumer<Life<T>> registrar)
    {
        sane(typeKey, "typeKey", action, "action");
        Life<T> cycle = new Life<>(typeKey);
        registrar.accept(cycle);
        return cycle(cycle);
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
}
