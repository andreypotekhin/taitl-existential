package com.taitl.existential.builders;

import java.util.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.invariants.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class TransactionBuilder
{
    ContextBuilder parent;
    String op;
    List<EvsBuilder> evsBuilders;
    List<Evs> evsList;

    public TransactionBuilder(ContextBuilder parentContext, String op)
    {
        this.parent = parentContext;
        this.op = op;
        this.evsBuilders = new ArrayList<>();
        this.evsList = new ArrayList<>();
    }

    public Transaction build()
    {
        Transaction tr = createInstance();
        tr.op(op);

        // TODO: bug! this code pushes all objects built with builders
        // invariant(Class) after the ones built with invariant(Invariant)

        for (EvsBuilder evsBuilder : evsBuilders)
        {
            evsList.add(evsBuilder.build());
        }

        for (Evs evs : this.evsList)
        {
            if (evs instanceof Invariant invariant)
            {
                tr.invariant(invariant);
            }
            else if (evs instanceof Effect effect)
            {
                tr.effect(effect);
            }
            else
            {
                throw new IllegalStateException("Unexpected class in ruleSet: " + evs);
            }
        }

        return tr;
    }

    public <T> InvariantBuilder<T> invariant(Class<T> cls)
    {
        sane(cls, "cls");
        InvariantBuilder<T> ib = new InvariantBuilder<>(this);
        evsBuilders.add(ib);
        return ib;
    }

    public <T> TransactionBuilder invariant(Invariant<T> invariant)
    {
        sane(invariant, "invariant");
        evsList.add(invariant);
        return this;
    }

    public <T> EffectBuilder<T> effect(Class<T> cls)
    {
        sane(cls, "cls");
        EffectBuilder<T> eb = new EffectBuilder<>(this);
        evsBuilders.add(eb);
        return eb;
    }

    public <T> TransactionBuilder effect(Effect<T> effect)
    {
        sane(effect, "effect");
        evsList.add(effect);
        return this;
    }

    // TODO: transaction()
    // TODO: intent()

    protected Transaction createInstance()
    {
        return parent.createTransactionInstance();
    }

    public TransactionBuilder done()
    {
        return this;
    }
}
