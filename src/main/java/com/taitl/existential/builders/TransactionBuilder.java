package com.taitl.existential.builders;

import java.util.*;
import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.invariants.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class TransactionBuilder
{
    ContextBuilder parent;
    String op;
    List<Supplier<? extends Evs<?>>> evsSuppliers;
    Supplier<? extends Transaction> transactionFactory;

    public TransactionBuilder(ContextBuilder parentContext, String op)
    {
        this.parent = parentContext;
        this.op = op;
        this.evsSuppliers = new ArrayList<>();
        this.transactionFactory = parent::createTransactionInstance;
    }

    public TransactionBuilder(ContextBuilder parentContext, Supplier<? extends Transaction> transactionFactory)
    {
        sane(parentContext, "parentContext", transactionFactory, "transactionFactory");
        this.parent = parentContext;
        this.op = null;
        this.evsSuppliers = new ArrayList<>();
        this.transactionFactory = transactionFactory;
    }

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
                if (evs instanceof Invariant invariant)
                {
                    tr.invariant(invariant);
                }
                else if (evs instanceof Effect effect)
                {
                    tr.effect(effect);
                }
                else if (evs instanceof Trancycle cycle)
                {
                    tr.cycle(cycle);
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

    public <T> InvariantBuilder<T> invariant(Class<T> cls)
    {
        sane(cls, "cls");
        InvariantBuilder<T> ib = new InvariantBuilder<>(this);
        evsSuppliers.add(() -> ib.build());
        return ib;
    }

    public <T> TransactionBuilder invariant(Invariant<T> invariant)
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

    public <T> TransactionBuilder effect(Effect<T> effect)
    {
        sane(effect, "effect");
        evsSuppliers.add(() -> effect);
        return this;
    }

    public <T extends Transaction> TransactionBuilder cycle(Trancycle<T> cycle)
    {
        sane(cycle, "cycle");
        evsSuppliers.add(() -> cycle);
        return this;
    }

    // TODO: intent()

    public <T extends Transaction> TransactionBuilder begin(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>() {
            {
                begin(action);
            }
        });
        return this;
    }

    public <T extends Transaction> TransactionBuilder commit(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>() {
            {
                commit(action);
            }
        });
        return this;
    }

    public <T extends Transaction> TransactionBuilder rollback(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>() {
            {
                rollback(action);
            }
        });
        return this;
    }

    public <T extends Transaction> TransactionBuilder checkpoint(Consumer<? super T> action)
    {
        sane(action, "action");
        cycle(new Trancycle<T>() {
            {
                checkpoint(action);
            }
        });
        return this;
    }

    public TransactionBuilder done()
    {
        return this;
    }

    protected Transaction createInstance()
    {
        return transactionFactory.get();
    }
}
