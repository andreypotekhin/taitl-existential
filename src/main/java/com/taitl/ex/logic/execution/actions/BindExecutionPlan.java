package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.ex.logic.execution.rules.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.transactions.*;

public class BindExecutionPlan
{
    @Logic
    protected RequiresReportTimeEvaluation requiresReportTimeEvaluation =
            Creator.create(RequiresReportTimeEvaluation.class);

    public void call(Tr tr, ExecutionPlan plan)
    {
        boolean begin = plan.requires(StageName.BEGIN)
                || requiresReportTimeEvaluation.in(tr, StageName.BEGIN);
        boolean immediate = plan.requires(StageName.IMMEDIATE)
                || requiresReportTimeEvaluation.in(tr, StageName.IMMEDIATE);
        tr.activateReportStages(begin, immediate);
    }
}
