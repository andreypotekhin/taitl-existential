package com.taitl.existential.configs;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Defines a single business operation as a set of {@link Context} objects
 * configured with constraints, invariants, intents, qualifiers, and effects.
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
     * Name of the business operation, e.g. "/app/docs/update",
     * or a wildcard name, "/app/docs/*".
     */
    protected String name;

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
     * Returns the operation name associated with this configuration.
     *
     * @return Operation name
     */
    public String name()
    {
        return name;
    }

    /**
     * Sets the operation name for this configuration.
     *
     * @param name Operation name
     */
    public void name(String name)
    {
        this.name = name;
    }

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
    public ConfigurationIndexes indexes()
    {
        return indexes(StageName.VALIDATION);
    }

    public ConfigurationIndexes indexes(StageName stageName)
    {
        sane(stageName, "stageName");
        ConfigurationIndexes indexes = stageIndexes.get(stageName);
        verify(indexes != null, "ConfigurationIndexes are missing for stage " + stageName);
        return indexes;
    }

}
