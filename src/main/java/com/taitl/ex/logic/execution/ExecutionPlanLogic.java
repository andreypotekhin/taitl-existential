package com.taitl.ex.logic.execution;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.transactions.*;

public class ExecutionPlanLogic
{
    @Logic
    protected CompileExecutionPlan compileExecutionPlan = Creator.create(CompileExecutionPlan.class);

    @Logic
    protected BindExecutionPlan bindExecutionPlan = Creator.create(BindExecutionPlan.class);

    public ExecutionPlan compile(String op, Config config)
    {
        return compileExecutionPlan.call(op, config);
    }

    public void bind(Tr tr, ExecutionPlan plan)
    {
        bindExecutionPlan.call(tr, plan);
    }
}
