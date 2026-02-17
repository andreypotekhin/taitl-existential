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

    public void call(Map<String, ConfigBuilder> configBuilders)
    {
        for (String op : configBuilders.keySet())
        {
            ConfigBuilder cb = configBuilders.get(op);
            Config config = cb.build(cl.ec());
            cl.registry().addConfig(config);
        }
    }
}
