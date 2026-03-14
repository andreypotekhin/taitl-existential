package com.taitl.ex.logic.configuration.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.builders.*;

/**
 * Provides a single ConfigBuilder instance for the Existential instance.
 */
public class CreateBuilders
{
    @Up
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

    protected ConfigBuilder createBuilder()
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
