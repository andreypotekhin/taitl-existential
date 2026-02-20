package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.existential.configs.*;

public class TraverseConfig
{
    IndexConfig indexes;

    public TraverseConfig(IndexConfig indexes)
    {
        this.indexes = indexes;
    }

    public void visit(Config config)
    {
        for (Context context : config.contexts())
        {
            indexes.onContext(context);
        }
    }
}
