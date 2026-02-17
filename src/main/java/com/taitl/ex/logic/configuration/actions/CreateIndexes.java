package com.taitl.ex.logic.configuration.actions;

import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

public class CreateIndexes
{
    protected ConfigurationLogic cl;

    public CreateIndexes(ConfigurationLogic cl)
    {
        this.cl = cl;
    }

    public void call(String op)
    {
        sane(op, "op");
        // Add all configured rules to config indexes in the order of declaration
        Config config = cl.registry().get(op);
        config.indexes().indexConfig(config);
    }
}
