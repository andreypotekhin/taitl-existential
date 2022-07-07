package com.taitl.existential.config.builders;

import com.taitl.existential.rules.RuleSet;

public interface RuleSetBuilder<T>
{
    public RuleSet<T> build();
}
