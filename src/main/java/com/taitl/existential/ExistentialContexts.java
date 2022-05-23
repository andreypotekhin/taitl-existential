package com.taitl.existential;

import java.util.List;

import com.taitl.existential.contexts.Context;
import com.taitl.existential.contexts.Contexts;
import com.taitl.existential.helper.Args;

public class ExistentialContexts
{
    public List<Context> get(String op)
    {
        Args.cool(op, "op");
        return Contexts.getcreate(op);
    }
}
