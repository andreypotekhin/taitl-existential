package com.taitl.ex.logic.configuration;

import java.util.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.Outcome.*;

/**
 * ConfigRegistry holds references to Configs, keyed by op name.
 */
public class ConfigRegistry
{
    protected Map<String, Config> configs = new LinkedHashMap<>();
    protected ConfigurationLogic cl;

    public ConfigRegistry(ConfigurationLogic cl)
    {
        this.cl = cl;
    }

    public boolean has(String op)
    {
        return configs.containsKey(op);
    }

    public Config get(String op)
    {
        sane(op, "op");
        Config o = configs.get(op);
        verify(o != null, String.format("Config not found for op '%s'", op));
        return o;
    }

    public Config remove(String op)
    {
        sane(op, "op");
        Config o = configs.get(op);
        verify(o != null, String.format("Config not found for op '%s'", op));
        synchronized (configs)
        {
            configs.remove(op);
        }
        return o;
    }

    public void addConfig(Config config)
    {
        sane(config, "config");
        String op = config.name();
        verify(!configs.containsKey(op),
                String.format("Cannot add Config for '%s' - config already exists", op));
        configs.put(op, config);
    }

    public boolean isEmpty()
    {
        return configs.isEmpty();
    }

    public Map<String, Config> configs()
    {
        return configs;
    }
}
