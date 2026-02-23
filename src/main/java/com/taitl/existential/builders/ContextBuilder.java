package com.taitl.existential.builders;

import java.util.*;
import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.invariants.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Configures a single Context inside a {@link com.taitl.existential.configs.Config}.
 * Collects invariants, effects, and optional transaction settings for the context.
 */
// TODO: add context() method to build child contexts
public class ContextBuilder
{
    ConfigBuilder parent;
    String op;
    List<Supplier<? extends Evs<?>>> evsSuppliers;
    Supplier<? extends Context> contextFactory;
    Supplier<? extends Transaction> transactionFactory;

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
        InvariantBuilder<T> ib = new InvariantBuilder<>(this);
        evsSuppliers.add(() -> ib.build());
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
        evsSuppliers.add(() -> invariant);
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
        EffectBuilder<T> eb = new EffectBuilder<>(this);
        evsSuppliers.add(() -> eb.build());
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
        evsSuppliers.add(() -> effect);
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
        return parent.context(name);
    }

    // TODO: intent()

    /**
     * Builds the configured context and returns the parent {@link ConfigBuilder}.
     *
     * @return Parent builder
     */
    public ConfigBuilder build()
    {
        verify(!evsSuppliers.isEmpty() || transactionFactory != null,
                "Cannot configure context without defining rules");

        Context context = createInstance();
        context.op(op);

        for (Supplier<? extends Evs<?>> supplier : evsSuppliers)
        {
            Evs<?> evs = supplier.get();
            if (evs instanceof Invariant<?>)
            {
                context.invariant((Invariant<?>) evs);
            }
            else if (evs instanceof Effect<?>)
            {
                context.effect((Effect<?>) evs);
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
        return parent;
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
}
