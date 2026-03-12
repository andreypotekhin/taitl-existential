package com.taitl.ex.logic.configuration.actions;

import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.builders.*;

/**
 * Provides a single ConfigBuilder instance for the Existential instance.
 */
public class CreateBuilders
{
    protected ConfigurationLogic cl;

    public CreateBuilders(ConfigurationLogic cl)
    {
        this.cl = cl;
    }

    public ConfigBuilder getCreateBuilder()
    {
        ConfigBuilder o = cl.configBuilder();
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
        ConfigBuilder o = new ConfigBuilder(cl.ex());
        synchronized (this)
        {
            ConfigBuilder existing = cl.configBuilder();
            if (existing != null)
            {
                return existing;
            }
            cl.configBuilder(o);
        }
        return o;
    }
}
