package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.configs.*;

public class TraverseConfig
{
    ConfigIndexes indexes;

    public TraverseConfig(ConfigIndexes indexes)
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
