package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.ex.logic.configuration.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

public class IndexConfig
{
    protected ConfigurationLogic cl;

    public IndexConfig(ConfigurationLogic cl)
    {
        this.cl = cl;
    }

    public void call(String op, Config config)
    {
        sane(op, "op", config, "config");
        ConfigIndexes indexes = config.indexes();
        // indexes.indexConfig(config);
        // TODO: Add all configured rules to config indexes in the order of declaration
    }
}
