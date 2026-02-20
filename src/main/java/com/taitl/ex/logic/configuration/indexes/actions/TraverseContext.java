package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.existential.evaluables.*;

public class TraverseContext implements Evaluator
{
    IndexConfig ic;

    public TraverseContext(IndexConfig ic)
    {
        this.ic = ic;
    }

    public <T> void visit(Ev<T> ev)
    {
        ic.onRule(ev);
    }
}
