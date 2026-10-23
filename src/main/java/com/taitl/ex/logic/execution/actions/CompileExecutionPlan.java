package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.ex.logic.execution.rules.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;

import java.util.*;

public class CompileExecutionPlan
{
    @Logic
    protected RequiresReportTimeEvaluation requiresReportTimeEvaluation =
            Creator.create(RequiresReportTimeEvaluation.class);

    public ExecutionPlan call(String op, Config config)
    {
        Set<StageName> reportStages = EnumSet.noneOf(StageName.class);
        for (StageName stageName : new StageName[] { StageName.BEGIN, StageName.IMMEDIATE })
        {
            if (requiresReportTimeEvaluation.in(config.indexes(op, stageName)))
            {
                reportStages.add(stageName);
            }
        }
        return new ExecutionPlan(reportStages);
    }
}
