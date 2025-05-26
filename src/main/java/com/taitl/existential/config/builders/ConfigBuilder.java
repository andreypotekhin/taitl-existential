package com.taitl.existential.config.builders;

import com.taitl.existential.contexts.OpContext;
import com.taitl.existential.helper.Args;
import com.taitl.existential.interfaces.Configurable;
import com.taitl.existential.invariants.Effect;
import com.taitl.existential.invariants.Invariant;
import com.taitl.existential.rules.RuleSet;

import java.util.ArrayList;
import java.util.List;

public class ConfigBuilder
{
    public enum TargetType
    {
        TYPE_CONTEXT, TYPE_TRANSACTION
    }

    TargetType type;
    OpContext parent;
    List<RuleSetBuilder> ruleSetBuilders;
    List<RuleSet> ruleSets;

    public ConfigBuilder(OpContext parentContext, TargetType type)
    {
        this.type = type;
        this.parent = parentContext;
        this.ruleSetBuilders = new ArrayList<>();
        this.ruleSets = new ArrayList<RuleSet>();
    }

    public Configurable build() {
        Configurable configurable = createInstance();

        // TODO: bug! this code pushes all objects built with builders
        // after the ones built with a direct call to require()

        for(RuleSetBuilder ruleSetBuilder : ruleSetBuilders) {
            ruleSets.add(ruleSetBuilder.build());
        }

        for(RuleSet ruleSet : ruleSets) {
            switch(ruleSet){
                case Invariant invariant:
                    configurable.ensure(invariant);
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
        InvariantBuilder ib = new InvariantBuilder(this);
        ruleSetBuilders.add(ib);
        return ib;
    }

    public <T> EffectBuilder<T> effect(Class<T> cls)
    {
        EffectBuilder eb = new EffectBuilder(this);
        ruleSetBuilders.add(eb);
        return eb;
    }

    public <T> ConfigBuilder require(RuleSet<T> ruleSet)
    {
        Args.cool(ruleSet, "ruleSet");
        ruleSets.add(ruleSet);
        return this;
    }

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
