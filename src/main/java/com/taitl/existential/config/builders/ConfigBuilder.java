package com.taitl.existential.config.builders;

import com.taitl.existential.contexts.OpContext;
import com.taitl.existential.instructions.Instructions;
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
    List<RuleSet> ruleSets;

    public ConfigBuilder(TargetType type, OpContext parentContext)
    {
        this.type = type;
        this.parent = parentContext;
        this.ruleSets = new ArrayList<RuleSet>();
    }

    public Configurable build() {
        Configurable configurable = createInstance();

        for(RuleSet ruleSet : ruleSets) {
            switch(ruleSet){
                case Invariant invariant:
                    configurable.require(invariant);
                    break;
                case Effect effect:
                    configurable.require(effect);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + ruleSet);
            }
        }

        return configurable;
    }

    protected Configurable createInstance()
    {
        switch (type)
        {
        // case TYPE_CONTEXT:
        // return parent.createContextInstance();
        // case TYPE_TRANSACTION:
        // return parent.createTransactionInstance();
        default:
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}
