package com.taitl.ex.domain.configuration;

import java.util.*;
import com.taitl.ex.logic.existential.*;
import com.taitl.existential.configuration.builders.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.helper.*;

/**
 * ConfigRegistry holds references to ConfigBuilders, keyed by op name.
 */
public class ConfigRegistry
{
    protected ExistentialConfigs ops;
    protected Map<String, ConfigBuilder> registry = new LinkedHashMap<>();

    public ConfigRegistry(ExistentialConfigs ops)
    {
        this.ops = ops;
    }

    public ConfigBuilder create(String name)
    {
        Args.cool(name, "name");
        ConfigBuilder o = new ConfigBuilder(name);
        synchronized (this)
        {
            for (Context context : ops.ex().contexts().createContexts(name))
            {
                o.addContext(context);
            }
            registry.put(name, o);
        }
        return o;
    }

    public boolean has(String id)
    {
        return registry.containsKey(id);
    }

    public ConfigBuilder get(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        ConfigBuilder o = registry.get(id);
        if (o == null)
        {
            throw new NotFoundException("Context not found, id=" + id);
        }
        return o;
    }

    public ConfigBuilder getcreate(String id)
    {
        Args.cool(id, "id");
        ConfigBuilder o = registry.get(id);
        return (o != null) ? o : create(id);
    }

    public ConfigBuilder remove(String id) throws NotFoundException
    {
        Args.cool(id, "id");
        ConfigBuilder o = registry.get(id);
        if (o == null)
        {
            throw new NotFoundException("Context not found, id=" + id);
        }
        synchronized (registry)
        {
            registry.remove(id);
        }
        return o;
    }

    public void createSubcontexts()
    {
        registry.forEach((key, op) -> op.createSubcontexts());
    }

    public boolean isEmpty()
    {
        return registry.isEmpty();
    }
}
