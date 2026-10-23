package com.taitl.ex.concrete;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Concrete state and behavior behind {@link Config}.
 */
public class ConcreteConfig
{
    protected List<Context> contexts;
    protected Map<StageName, ConfigurationIndexes> stageIndexes;
    protected OperationIndexes operationIndexes;
    protected boolean useFullClassNames;

    public void addContext(Context context)
    {
        sane(context, "context");
        verify(!contexts.contains(context), "This context is already added");
        contexts.add(context);
    }

    public List<Context> contexts()
    {
        return contexts;
    }

    public ConfigurationIndexes indexes(StageName stageName)
    {
        sane(stageName, "stageName");
        ConfigurationIndexes indexes = stageIndexes.get(stageName);
        verify(indexes != null, "ConfigurationIndexes are missing for stage " + stageName);
        return indexes;
    }

    public ConfigurationIndexes indexes(String op, StageName stageName, Config config)
    {
        sane(op, "op", stageName, "stageName");
        String opKey = op.trim();
        verify(!opKey.isEmpty(), "op cannot be blank");
        return operationIndexes.resolve(opKey, config, useFullClassNames).get(stageName);
    }

    public boolean useFullClassNames()
    {
        return useFullClassNames;
    }

    public void useFullClassNames(boolean useFullClassNames)
    {
        this.useFullClassNames = useFullClassNames;
        for (ConfigurationIndexes indexes : stageIndexes.values())
        {
            indexes.useFullClassNames(useFullClassNames);
        }
        for (Map<StageName, ConfigurationIndexes> opIndexes : operationIndexes.values())
        {
            for (ConfigurationIndexes indexes : opIndexes.values())
            {
                indexes.useFullClassNames(useFullClassNames);
            }
        }
    }

}
