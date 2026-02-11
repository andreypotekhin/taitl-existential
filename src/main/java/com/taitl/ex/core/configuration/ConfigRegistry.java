package com.taitl.ex.core.configuration;

import java.util.*;
import com.taitl.ex.core.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.exceptions.*;

import static com.taitl.ex.common.helper.Args.*;

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
        sane(name, "name");
        ConfigBuilder o = new ConfigBuilder(name);
        synchronized (this)
        {
            for (Context context : ops.ex().contexts().getContexts(name))
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
        sane(id, "id");
        ConfigBuilder o = registry.get(id);
        if (o == null)
        {
            throw new NotFoundException("Context not found, id=" + id);
        }
        return o;
    }

    public ConfigBuilder getcreate(String id)
    {
        sane(id, "id");
        ConfigBuilder o = registry.get(id);
        return (o != null) ? o : create(id);
    }

    public ConfigBuilder remove(String id) throws NotFoundException
    {
        sane(id, "id");
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

    public void finalizeConfiguration()
    {
        createSubcontexts();
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
