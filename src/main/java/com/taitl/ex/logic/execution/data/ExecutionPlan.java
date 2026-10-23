package com.taitl.ex.logic.execution.data;

import com.taitl.existential.constants.*;

import java.util.*;

/**
 * The operation's compiled decision about stages that must run when an entity is reported.
 * The stage set is copied at construction and shared across transactions after configuration
 * is finalized. It records stage participation, rather than handlers or reported entities.
 *
 * Binding combines this shared profile with transaction-local handlers and intents. A plan
 * with no report stages allows validation-only reporting to defer entity evaluation until
 * a transaction boundary while preserving lifecycle callbacks.
 */
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
