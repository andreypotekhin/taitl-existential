package com.taitl.exlogic.unused.builders;

import java.util.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.interfaces.*;
import com.taitl.existential.invariants.*;
import com.taitl.existential.ops.*;
import com.taitl.existential.rules.*;

public class ConfigBuilder
{
    public enum TargetType
    {
        TYPE_CONTEXT, TYPE_TRANSACTION
    }

    Op parent;
    String name;
    TargetType type;
    List<RuleSetBuilder> ruleSetBuilders;
    List<RuleSet> ruleSets;

    public ConfigBuilder(Op parentContext, String name, TargetType type)
    {
        this.parent = parentContext;
        this.name = name;
        this.type = type;
        this.ruleSetBuilders = new ArrayList<>();
        this.ruleSets = new ArrayList<RuleSet>();
    }

    public Configurable build() {
        Configurable configurable = createInstance();
        configurable.name(name);

        // TODO: bug! this code pushes all objects built with builders
        // after the ones built with a direct call to require()

        for(RuleSetBuilder ruleSetBuilder : ruleSetBuilders) {
            ruleSets.add(ruleSetBuilder.build());
        }

        for(RuleSet ruleSet : ruleSets) {
            switch(ruleSet){
                case Invariant invariant:
                    configurable.enforce(invariant);
                    break;
                case Effect effect:
                    configurable.cause(effect);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + ruleSet);
            }
        }

        return configurable;
    }

    public <T> InvariantBuilder<T> invariant(Class<T> cls)
    {
        Args.cool(cls, "cls");
        InvariantBuilder<T> ib = new InvariantBuilder<>(this);
        ruleSetBuilders.add(ib);
        return ib;
    }

    public <T> ConfigBuilder invariant(Invariant<T> invariant)
    {
        Args.cool(invariant, "invariant");
        ruleSets.add(invariant);
        return this;
    }

    public <T> EffectBuilder<T> effect(Class<T> cls)
    {
        Args.cool(cls, "cls");
        EffectBuilder<T> eb = new EffectBuilder<>(this);
        ruleSetBuilders.add(eb);
        return eb;
    }

    public <T> ConfigBuilder effect(Effect<T> effect)
    {
        Args.cool(effect, "effect");
        ruleSets.add(effect);
        return this;
    }

    // public <T> ConfigBuilder require(RuleSet<T> ruleSet)
    // {
    // Args.cool(ruleSet, "ruleSet");
    // ruleSets.add(ruleSet);
    // return this;
    // }

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

    public ConfigBuilder done()
    {
        return this;
    }
}
