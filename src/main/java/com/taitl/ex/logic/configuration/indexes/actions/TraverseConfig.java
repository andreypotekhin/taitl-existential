package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.existential.configs.*;

public class TraverseConfig
{
    IndexConfig ic;

    public TraverseConfig(IndexConfig ic)
    {
        this.ic = ic;
    }

    public void visit(Config config)
    {
        for (Context context : config.contexts())
        {
            ic.onContext(context);
        }
    }
}
