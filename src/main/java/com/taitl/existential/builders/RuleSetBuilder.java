package com.taitl.existential.builders;

import com.taitl.existential.rules.RuleSet;

public interface RuleSetBuilder<T>
{
    public RuleSet<T> build();
}
