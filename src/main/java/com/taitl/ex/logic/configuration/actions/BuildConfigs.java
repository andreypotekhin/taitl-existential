package com.taitl.ex.logic.configuration.actions;

import java.util.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;

public class BuildConfigs
{
    protected ConfigurationLogic cl;

    public BuildConfigs(ConfigurationLogic cl)
    {
        this.cl = cl;
    }

    /**
     * Builds Config from ConfigBuilder and adds it to ConfigRegistry.
     */
    public void call(Map<String, ConfigBuilder> configBuilders)
    {
        for (ConfigBuilder cb : configBuilders.values())
        {
            Config config = cb.build(cl.ec());
            cl.registry().addConfig(config);

            Set<String> ops = new LinkedHashSet<>();
            for (Context context : config.contexts())
            {
                ops.add(context.name());
            }
            for (String op : ops)
            {
                cl.onFinishConfiguration(op);
            }
        }
    }
}
