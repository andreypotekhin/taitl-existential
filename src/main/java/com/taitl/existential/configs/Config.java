package com.taitl.existential.configs;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.constants.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

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
    /**
     * Contexts that apply to this operation. This includes the main context
     * (e.g. "/app/docs/update") as well as any matching wildcard contexts
     * (e.g. "/app/docs/*").
     */
    protected List<Context> contexts = new ArrayList<>();

    /** Transaction factory */
    protected Supplier<? extends Context> contextFactory = Context.FACTORY;

    /**
     * Configuration indexes for performance.
     */
    protected Map<StageName, ConfigurationIndexes> stageIndexes = new EnumMap<>(StageName.class);
    protected Map<String, Map<StageName, ConfigurationIndexes>> opStageIndexes = new LinkedHashMap<>();
    protected boolean useFullClassNames;

    public Config()
    {
        for (StageName stageName : StageName.values())
        {
            stageIndexes.put(stageName, Creator.create(ConfigurationIndexes.class));
        }
    }

    /**
     * Adds a {@link Context} instance to the operation.
     * Called by {@code ConfigRegistry.create(op)}.
     *
     * @param cont Context to add
     */
    public void addContext(Context cont)
    {
        sane(cont, "cont");
        verify(!contexts.contains(cont), "This context is already added");
        contexts.add(cont);
    }

    /* Attributes */

    /**
     * Returns all contexts declared for this operation, in declaration order.
     *
     * @return Ordered list of contexts
     */
    public List<Context> contexts()
    {
        return contexts;
    }

    /**
     * Returns configuration indexes used for rule lookup and evaluation.
     *
     * @return Configuration indexes
     */
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
            ConfigurationIndexes indexes = Creator.create(ConfigurationIndexes.class);
            indexes.useFullClassNames(useFullClassNames);
            opIndexes.put(stageName, indexes);
        }
        return opIndexes;
    }

}
