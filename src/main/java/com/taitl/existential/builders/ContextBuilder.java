package com.taitl.existential.builders;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.invariants.*;

public class ContextBuilder
{
    public enum TargetType
    {
        TYPE_CONTEXT, TYPE_TRANSACTION
    }

    ConfigBuilder parent;
    String name;
    TargetType type;
    List<EvsBuilder> evsBuilders;
    List<Evs> evsList;

    public ContextBuilder(ConfigBuilder parentConfig, String name, TargetType type)
    {
        this.parent = parentConfig;
        this.name = name;
        this.type = type;
        this.evsBuilders = new ArrayList<>();
        this.evsList = new ArrayList<>();
    }

    public Configurable build()
    {
        Configurable configurable = createInstance();
        configurable.name(name);

        // TODO: bug! this code pushes all objects built with builders
        // after the ones built with a direct call to require()

        for (EvsBuilder evsBuilder : evsBuilders)
        {
            evsList.add(evsBuilder.build());
        }

        for (Evs evs : this.evsList)
        {
            if (evs instanceof Invariant invariant)
            {
                configurable.invariant(invariant);
            }
            else if (evs instanceof Effect effect)
            {
                configurable.effect(effect);
            }
            else
            {
                throw new IllegalStateException("Unexpected class in ruleSet: " + evs);
            }
        }

        return configurable;
    }

    public <T> InvariantBuilder<T> invariant(Class<T> cls)
    {
        Args.sane(cls, "cls");
        InvariantBuilder<T> ib = new InvariantBuilder<>(this);
        evsBuilders.add(ib);
        return ib;
    }

    public <T> ContextBuilder invariant(Invariant<T> invariant)
    {
        Args.sane(invariant, "invariant");
        evsList.add(invariant);
        return this;
    }

    public <T> EffectBuilder<T> effect(Class<T> cls)
    {
        Args.sane(cls, "cls");
        EffectBuilder<T> eb = new EffectBuilder<>(this);
        evsBuilders.add(eb);
        return eb;
    }

    public <T> ContextBuilder effect(Effect<T> effect)
    {
        Args.sane(effect, "effect");
        evsList.add(effect);
        return this;
    }

    // public <T> ConfigBuilder require(RuleSet<T> ruleSet)
    // {
    // Args.cool(ruleSet, "ruleSet");
    // ruleSets.add(ruleSet);
    // return this;
    // }

    // TODO: transaction()
    // TODO: intent()

    protected Configurable createInstance()
    {
        switch (type)
        {
        case TYPE_CONTEXT:
            return parent.createContextInstance();
        case TYPE_TRANSACTION:
            return parent.createTransactionInstance();
        default:
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    public ContextBuilder done()
    {
        return this;
    }
}
