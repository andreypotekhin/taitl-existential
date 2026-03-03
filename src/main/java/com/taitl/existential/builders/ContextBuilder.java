package com.taitl.existential.builders;

import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Configures a single Context inside a {@link com.taitl.existential.configs.Config}.
 * Collects invariants, effects, intents, and optional transaction settings for the context.
 */
public class ContextBuilder
{
    ConfigBuilder parent;
    String op;
    List<Supplier<? extends Evs<?>>> evsSuppliers;
    List<StageName> evsStages;
    Supplier<? extends Context> contextFactory;
    Supplier<? extends Transaction> transactionFactory;
    StageName stageCursor;
    boolean built;

    /**
     * Creates a context builder for a specific operation name.
     *
     * @param parentConfig
     *            Parent config builder
     * @param op
     *            Operation name to configure
     */
    public ContextBuilder(ConfigBuilder parentConfig, String op)
    {
        this.parent = parentConfig;
        this.op = op;
        this.evsSuppliers = new ArrayList<>();
        this.evsStages = new ArrayList<>();
    }

    /**
     * Starts building an {@link Invariant} for the given subject type.
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
     * Registers an already-built invariant with this context.
     *
     * @param invariant
     *            Invariant to register
     * @param <T>
     *            Subject type for the invariant
     * @return This builder for chaining
     */
    public <T> ContextBuilder invariant(Invariant<T> invariant)
    {
        sane(invariant, "invariant");
        register(() -> invariant, StageName.VALIDATION);
        return this;
    }

    /**
     * Starts building an {@link Effect} for the given subject type.
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
     * Registers an already-built effect with this context.
     *
     * @param effect
     *            Effect to register
     * @param <T>
     *            Subject type for the effect
     * @return This builder for chaining
     */
    public <T> ContextBuilder effect(Effect<T> effect)
    {
        sane(effect, "effect");
        register(() -> effect, StageName.VALIDATION);
        return this;
    }

    /**
     * Starts building a sibling context on the parent builder.
     *
     * @param name
     *            Context name to build
     * @return Context builder for the named context
     */
    public ContextBuilder context(String name)
    {
        sane(name, "name");
        if (hasPendingConfiguration())
        {
            buildContext();
        }
        return parent.context(parent.requireContextNameMatchesParentContext(name, op));
    }

    /**
     * Starts building a sibling context with the same operation key.
     *
     * @return Context builder for this context operation key
     */
    public ContextBuilder context()
    {
        if (hasPendingConfiguration())
        {
            buildContext();
        }
        return parent.context(op);
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
     * Registers an already-built intent with this context.
     *
     * @param intent
     *            Intent to register
     * @param <T>
     *            Subject type for the intent
     * @return This builder for chaining
     */
    public <T> ContextBuilder intent(Intent<T> intent)
    {
        sane(intent, "intent");
        register(() -> intent, StageName.IMMEDIATE);
        return this;
    }

    public ContextBuilder precondition()
    {
        stageCursor = StageName.PRECONDITION;
        return this;
    }

    public ContextBuilder immediate()
    {
        stageCursor = StageName.IMMEDIATE;
        return this;
    }

    public ContextBuilder validation()
    {
        stageCursor = StageName.VALIDATION;
        return this;
    }

    void buildContext()
    {
        if (built)
        {
            return;
        }
        verify(!evsSuppliers.isEmpty() || transactionFactory != null,
                "Cannot configure context without defining rules");

        Context context = createInstance();
        context.op(op);

        for (int i = 0; i < evsSuppliers.size(); i++)
        {
            Evs<?> evs = evsSuppliers.get(i).get();
            StageName stageName = evsStages.get(i);
            if (evs instanceof Invariant<?> || evs instanceof Effect<?> || evs instanceof Intent<?>)
            {
                context.add(evs, stageName);
            }
            else
            {
                throw new IllegalStateException("Unexpected class in ruleSet: " + evs);
            }
        }

        if (transactionFactory != null)
        {
            context.transaction(transactionFactory);
        }
        parent.context(context);
        built = true;
    }

    /**
     * Starts building a transaction for the given operation name.
     *
     * @param name
     *            Operation name for the transaction
     * @return Transaction builder
     */
    public TransactionBuilder transaction(String name)
    {
        return new TransactionBuilder(this, name);
    }

    /**
     * No-op convenience for fluent chaining.
     *
     * @return This builder
     */
    public ContextBuilder done()
    {
        return this;
    }

    /**
     * Starts building a transaction using the supplied factory.
     *
     * @param supplier
     *            Factory for transaction instances
     * @return Transaction builder
     */
    public TransactionBuilder transaction(Supplier<? extends Transaction> supplier)
    {
        sane(supplier, "supplier");
        return new TransactionBuilder(this, supplier);
    }

    Context createInstance()
    {
        if (contextFactory != null)
        {
            return contextFactory.get();
        }
        return parent.createContextInstance();
    }

    /**
     * Overrides the context factory used when building this context.
     *
     * @param supplier
     *            Context factory to use
     * @return This builder for chaining
     */
    public ContextBuilder contextFactory(Supplier<? extends Context> supplier)
    {
        sane(supplier, "supplier");
        contextFactory = supplier;
        return this;
    }

    Transaction createTransactionInstance()
    {
        return parent.createTransactionInstance();
    }

    protected void register(Supplier<? extends Evs<?>> supplier, StageName defaultStage)
    {
        sane(supplier, "supplier", defaultStage, "defaultStage");
        evsSuppliers.add(supplier);
        evsStages.add(stageCursor != null ? stageCursor : defaultStage);
    }

    protected boolean hasPendingConfiguration()
    {
        return !built && (!evsSuppliers.isEmpty() || transactionFactory != null);
    }
}
