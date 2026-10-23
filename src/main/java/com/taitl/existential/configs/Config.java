package com.taitl.existential.configs;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.constants.*;

import java.util.*;

/**
 * Defines the set of {@link Context} objects and indexes applicable for a
 * resolved operation key.
 *
 * Multiple contexts may apply to the same business operation: the main
 * context, its parent contexts, and any matching wildcard contexts.
 *
 * Contexts are stored in the order they are declared.
 *
 * @see Context
 */
public class Config
{
    protected final ConcreteConfig concrete;

    public Config()
    {
        concrete = createBuilder().build();
    }

    public void addContext(Context context)
    {
        concrete.addContext(context);
    }

    public List<Context> contexts()
    {
        return concrete.contexts();
    }

    public ConfigurationIndexes indexes(StageName stageName)
    {
        return concrete.indexes(stageName);
    }

    /**
     * Returns the configured rule indexes for a concrete operation and processing stage.
     *
     * @param op concrete operation key
     * @param stageName processing stage
     * @return the complete indexes for that operation and stage
     */
    public ConfigurationIndexes indexes(String op, StageName stageName)
    {
        return concrete.indexes(op, stageName, this);
    }

    public boolean useFullClassNames()
    {
        return concrete.useFullClassNames();
    }

    public void useFullClassNames(boolean useFullClassNames)
    {
        concrete.useFullClassNames(useFullClassNames);
    }

    protected ConcreteConfigBuilder createBuilder()
    {
        return Creator.create(ConcreteConfigBuilder.class);
    }
}
