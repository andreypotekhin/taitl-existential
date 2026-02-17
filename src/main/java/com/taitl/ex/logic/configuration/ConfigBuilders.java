package com.taitl.ex.logic.configuration;

import java.util.*;
import com.taitl.ex.core.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.contexts.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Provides ConfigBuilder instances and builds resulting configs, outputting to ConfigRegistry.
 */
public class ConfigBuilders
{
    protected ConfigurationLogic configurationLogic;
    protected ConfigRegistry configRegistry;
    protected ExistentialConfigs ec;
    protected Map<String, ConfigBuilder> configBuilders = new LinkedHashMap<>();

    public ConfigBuilders(ConfigurationLogic configurationLogic)
    {
        this.configurationLogic = configurationLogic;
        this.configRegistry = configurationLogic.registry();
        this.ec = configurationLogic.ec();
    }

    public ConfigBuilder createBuilder(String name)
    {
        sane(name, "name");
        ConfigBuilder o = new ConfigBuilder(name);
        synchronized (this)
        {
            for (Context context : configurationLogic.contexts().buildContexts(name))
            {
                o.addContext(context);
            }
            configBuilders.put(name, o);
        }
        return o;
    }

    public ConfigBuilder getcreateBuilder(String id)
    {
        sane(id, "id");
        ConfigBuilder o = configBuilders.get(id);
        return (o != null) ? o : createBuilder(id);
    }

    public void finalizeConfiguration()
    {
        buildConfigs();
    }

    public void buildConfigs()
    {
        for (String op : configBuilders.keySet())
        {
            ConfigBuilder cb = configBuilders.get(op);
            Config config = cb.build(ec);
            configRegistry.addConfig(config);
        }
        // Configuration done.
        // Prevent any further use of configBuilders.
        configBuilders.clear();
    }
}
