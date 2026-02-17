package com.taitl.ex.logic.configuration;

import java.util.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.exceptions.*;

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

    public boolean has(String id)
    {
        return configs.containsKey(id);
    }

    public Config get(String id) throws NotFoundException
    {
        sane(id, "id");
        Config o = configs.get(id);
        if (o == null)
        {
            throw new NotFoundException("Context not found, id=" + id);
        }
        return o;
    }

    public Config remove(String id) throws NotFoundException
    {
        sane(id, "id");
        Config o = configs.get(id);
        if (o == null)
        {
            throw new NotFoundException("Context not found, id=" + id);
        }
        synchronized (configs)
        {
            configs.remove(id);
        }
        return o;
    }

    public void addConfig(Config config)
    {
        sane(config, "config");
        String op = config.name();
        verify(!configs.containsKey(op),
                String.format("Cannot add Config for '%' - config already exists", op));
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
