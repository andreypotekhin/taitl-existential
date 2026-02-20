package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.evaluables.*;

public class TraverseContext implements Evaluator
{
    ConfigIndexes indexes;

    public TraverseContext(ConfigIndexes indexes)
    {
        this.indexes = indexes;
    }

    public <T> void visit(Ev<T> ev)
    {
        indexes.onRule(ev);
    }
}
