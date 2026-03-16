package com.taitl.ex.concrete;

import com.taitl.ex.logic.configuration.indexes.*;
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
    protected Map<String, Map<StageName, ConfigurationIndexes>> opStageIndexes;
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

    public ConfigurationIndexes indexes(String op, StageName stageName)
    {
        sane(op, "op", stageName, "stageName");
        String opKey = op.trim();
        verify(!opKey.isEmpty(), "op cannot be blank");
        Map<StageName, ConfigurationIndexes> opIndexes = opStageIndexes.get(opKey);
        if (opIndexes == null)
        {
            synchronized (opStageIndexes)
            {
                opIndexes = opStageIndexes.get(opKey);
                if (opIndexes == null)
                {
                    opIndexes = createOpIndexes();
                    opStageIndexes.put(opKey, opIndexes);
                }
            }
        }
        ConfigurationIndexes indexes = opIndexes.get(stageName);
        verify(indexes != null, "ConfigurationIndexes are missing for op/stage " + opKey + "/" + stageName);
        return indexes;
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
        for (Map<StageName, ConfigurationIndexes> opIndexes : opStageIndexes.values())
        {
            for (ConfigurationIndexes indexes : opIndexes.values())
            {
                indexes.useFullClassNames(useFullClassNames);
            }
        }
    }

    protected Map<StageName, ConfigurationIndexes> createOpIndexes()
    {
        Map<StageName, ConfigurationIndexes> opIndexes = new EnumMap<>(StageName.class);
        for (StageName stageName : StageName.values())
        {
            ConfigurationIndexes indexes = new ConcreteConfigBuilder()
                    .createIndexes(useFullClassNames);
            opIndexes.put(stageName, indexes);
        }
        return opIndexes;
    }
}
