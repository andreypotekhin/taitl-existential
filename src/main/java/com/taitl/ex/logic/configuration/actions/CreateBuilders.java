package com.taitl.ex.logic.configuration.actions;

import java.util.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.builders.*;

/**
 * Provides ConfigBuilder instances and builds the Configs,
 * also adding to ConfigurationLogic.
 */
public class CreateBuilders
{
    protected static final String INSTANCE_BUILDER_KEY = "_instance";
    protected Map<String, ConfigBuilder> configBuilders;
    protected ConfigurationLogic cl;

    public CreateBuilders(ConfigurationLogic cl)
    {
        this.cl = cl;
        this.configBuilders = cl.configBuilders();
    }

    public ConfigBuilder getCreateBuilder()
    {
        ConfigBuilder o = configBuilders.get(INSTANCE_BUILDER_KEY);
        return (o != null) ? o : createBuilder();
    }

    /**
     * @deprecated Use {@link #getCreateBuilder()} instead.
     */
    @Deprecated
    public ConfigBuilder getcreateBuilder()
    {
        return getCreateBuilder();
    }

    public ConfigBuilder createBuilder()
    {
        ConfigBuilder o = new ConfigBuilder();
        synchronized (this)
        {
            ConfigBuilder existing = configBuilders.get(INSTANCE_BUILDER_KEY);
            if (existing != null)
            {
                return existing;
            }
            configBuilders.put(INSTANCE_BUILDER_KEY, o);
        }
        return o;
    }
}
