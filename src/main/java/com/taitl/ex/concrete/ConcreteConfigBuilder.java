package com.taitl.ex.concrete;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.constants.*;

import java.util.*;

public class ConcreteConfigBuilder
{
    protected boolean useFullClassNames;

    public ConcreteConfig build()
    {
        ConcreteConfig result = new ConcreteConfig();
        result.contexts = new ArrayList<>();
        result.stageIndexes = new EnumMap<>(StageName.class);
        result.opStageIndexes = new LinkedHashMap<>();
        result.useFullClassNames = useFullClassNames;
        for (StageName stageName : StageName.values())
        {
            result.stageIndexes.put(stageName, createIndexes(useFullClassNames));
        }
        return result;
    }

    public ConcreteConfigBuilder useFullClassNames(boolean useFullClassNames)
    {
        this.useFullClassNames = useFullClassNames;
        return this;
    }

    protected ConfigurationIndexes createIndexes(boolean useFullClassNames)
    {
        ConfigurationIndexes indexes = Creator.create(ConfigurationIndexes.class);
        indexes.useFullClassNames(useFullClassNames);
        return indexes;
    }
}
