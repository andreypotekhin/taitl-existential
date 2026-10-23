package com.taitl.ex.logic.execution.data;

import com.taitl.existential.constants.*;

import java.util.*;

public class ExecutionPlan
{
    private final EnumSet<StageName> reportStages;

    public ExecutionPlan(Set<StageName> reportStages)
    {
        this.reportStages = reportStages.isEmpty()
                ? EnumSet.noneOf(StageName.class)
                : EnumSet.copyOf(reportStages);
    }

    public boolean requires(StageName stageName)
    {
        return reportStages.contains(stageName);
    }
}
