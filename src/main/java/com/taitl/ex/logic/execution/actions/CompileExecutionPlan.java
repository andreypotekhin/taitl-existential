package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.ex.logic.execution.rules.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;

import java.util.*;

/**
 * Compiles the shared report-stage profile from an operation's finalized indexes.
 * BEGIN and IMMEDIATE require report-time work when their indexes contain handlers or intents;
 * other stages remain controlled by their transaction boundaries.
 *
 * The resulting ExecutionPlan contains only stage participation. Handler selection and
 * transaction-local additions are resolved separately, so compilation retains no payload.
 */
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
