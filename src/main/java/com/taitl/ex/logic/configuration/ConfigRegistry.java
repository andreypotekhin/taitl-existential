package com.taitl.ex.logic.configuration;

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
    protected ConfigurationLogic configurationLogic;
    protected ExistentialConfigs ec;
    protected Map<String, ConfigBuilder> configBuilders = new LinkedHashMap<>();

    public ConfigRegistry(ConfigurationLogic configurationLogic)
    {
        this.configurationLogic = configurationLogic;
        this.ec = configurationLogic.ec();
    }

    public ConfigBuilder create(String name)
    {
        sane(name, "name");
        ConfigBuilder o = new ConfigBuilder(name);
        synchronized (this)
        {
            for (Context context : ec.ex().contexts().getContexts(name))
            {
                o.addContext(context);
            }
            configBuilders.put(name, o);
        }
        return o;
    }

    public boolean has(String id)
    {
        return configBuilders.containsKey(id);
    }

    public ConfigBuilder get(String id) throws NotFoundException
    {
        sane(id, "id");
        ConfigBuilder o = configBuilders.get(id);
        if (o == null)
        {
            throw new NotFoundException("Context not found, id=" + id);
        }
        return o;
    }

    public ConfigBuilder getcreate(String id)
    {
        sane(id, "id");
        ConfigBuilder o = configBuilders.get(id);
        return (o != null) ? o : create(id);
    }

    public ConfigBuilder remove(String id) throws NotFoundException
    {
        sane(id, "id");
        ConfigBuilder o = configBuilders.get(id);
        if (o == null)
        {
            throw new NotFoundException("Context not found, id=" + id);
        }
        synchronized (configBuilders)
        {
            configBuilders.remove(id);
        }
        return o;
    }

    public void finalizeConfiguration()
    {
        buildContexts();
    }

    public void buildContexts()
    {
        configBuilders.forEach((key, op) -> op.build(ec));
    }

    public boolean isEmpty()
    {
        return configBuilders.isEmpty();
    }
}
