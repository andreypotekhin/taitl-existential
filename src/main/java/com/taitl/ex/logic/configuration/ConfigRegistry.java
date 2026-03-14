package com.taitl.ex.logic.configuration;

import com.taitl.ex.common.annotations.*;
import com.taitl.existential.configs.*;
import com.taitl.ex.logic.configuration.rules.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.Outcome.*;

/**
 * ConfigRegistry holds a single Config for the Existential instance.
 */
public class ConfigRegistry
{
    @Up
    protected ConfigurationLogic cl;
    protected Config config;

    public ConfigRegistry(ConfigurationLogic cl)
    {
        this.cl = cl;
    }

    public boolean has(String op)
    {
        return config != null && hasMatchingContext(op);
    }

    public Config get(String op)
    {
        sane(op, "op");
        verify(config != null, String.format("Config not found for op '%s'", op));
        verify(hasMatchingContext(op), String.format("Config not found for op '%s'", op));
        return config;
    }

    public Config remove(String op)
    {
        sane(op, "op");
        verify(config != null, String.format("Config not found for op '%s'", op));
        verify(hasMatchingContext(op), String.format("Config not found for op '%s'", op));
        synchronized (this)
        {
            Config prev = config;
            config = null;
            return prev;
        }
    }

    public void addConfig(Config config)
    {
        sane(config, "config");
        verify(this.config == null, "Cannot add Config - config already exists");
        this.config = config;
    }

    public boolean isEmpty()
    {
        return config == null;
    }

    protected boolean hasMatchingContext(String op)
    {
        sane(op, "op");
        for (Context context : config.contexts())
        {
            if (MatchParentName.matches(op, context.name()))
            {
                return true;
            }
        }
        return false;
    }
}
