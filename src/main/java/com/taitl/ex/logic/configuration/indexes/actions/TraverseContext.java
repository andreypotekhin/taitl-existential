package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.existential.evaluables.*;

public class TraverseContext implements Evaluator
{
    IndexConfig indexes;

    public TraverseContext(IndexConfig indexes)
    {
        this.indexes = indexes;
    }

    public <T> void visit(Ev<T> ev)
    {
        indexes.onRule(ev);
    }
}
