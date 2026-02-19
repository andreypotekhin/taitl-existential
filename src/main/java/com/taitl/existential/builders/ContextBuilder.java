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

    public ContextBuilder(ConfigBuilder parentConfig, String op)
    {
        this.parent = parentConfig;
        this.op = op;
        this.evsSuppliers = new ArrayList<>();
    }

    public <T> InvariantBuilder<T> invariant(Class<T> cls)
    {
        sane(cls, "cls");
        InvariantBuilder<T> ib = new InvariantBuilder<>(this);
        evsSuppliers.add(() -> ib.build());
        return ib;
    }

    public <T> ContextBuilder invariant(Invariant<T> invariant)
    {
        sane(invariant, "invariant");
        evsSuppliers.add(() -> invariant);
        return this;
    }

    public <T> EffectBuilder<T> effect(Class<T> cls)
    {
        sane(cls, "cls");
        EffectBuilder<T> eb = new EffectBuilder<>(this);
        evsSuppliers.add(() -> eb.build());
        return eb;
    }

    public <T> ContextBuilder effect(Effect<T> effect)
    {
        sane(effect, "effect");
        evsSuppliers.add(() -> effect);
        return this;
    }

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
            if (evs instanceof Invariant invariant)
            {
                context.invariant(invariant);
            }
            else if (evs instanceof Effect effect)
            {
                context.effect(effect);
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
        return parent;
    }

    public TransactionBuilder transaction(String name)
    {
        return new TransactionBuilder(this, name);
    }

    public ContextBuilder done()
    {
        return this;
    }

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
