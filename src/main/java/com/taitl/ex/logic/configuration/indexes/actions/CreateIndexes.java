package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

public class CreateIndexes
{
    protected ConfigurationLogic cl;
    protected IndexConfig indexConfig;

    public CreateIndexes(ConfigurationLogic cl)
    {
        this.cl = cl;
        this.indexConfig = new IndexConfig(cl);
    }

    public void call(String op)
    {
        sane(op, "op");
        Config config = cl.registry().get(op);
        indexConfig.call(op, config);
    }
}
