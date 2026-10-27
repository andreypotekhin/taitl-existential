package com.taitl.ex.logic.diagnostics;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.diagnostics.actions.*;
import com.taitl.ex.logic.diagnostics.data.*;
import com.taitl.ex.logic.evaluation.events.actions.*;
import com.taitl.existential.diagnostics.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import java.util.*;

public class DiagnosticLogic
{
    @Logic
    protected BuildDiagnostic build = Creator.create(BuildDiagnostic.class);
    @Logic
    protected DiagnoseFailure diagnose = Creator.create(DiagnoseFailure.class);
    @Logic
    protected RenderDiagnostic render = Creator.create(RenderDiagnostic.class);

    public String render(Diagnostic diagnostic)
    {
        return render.call(diagnostic);
    }

    public RuntimeException failure(RuntimeException failure, String operation, RuleOrigin origin, RuntimeKey<?> key,
            StageName stage, String boundary, boolean intent, boolean stopped)
    {
        try
        {
            boolean ordinary = failure instanceof ExistentialException
                    && OnException.constraintViolation((ExistentialException) failure);
            DiagnosticCode code = ordinary ? (intent ? DiagnosticCode.INTENT_REJECTED : DiagnosticCode.RULE_REJECTED)
                    : DiagnosticCode.CALLBACK_FAILED;
            Diagnostic diagnostic = build.call(code, operation, origin, key, stage, boundary, stopped);
            if (intent)
            {
                Throwable cause =
                        failure instanceof LibraryFailure && failure.getCause() != null ? failure.getCause() : failure;
                IntentViolation result = new IntentViolation("Intent evaluation failed", cause,
                        new FailureSnapshot(diagnostic, "Intent evaluation failed"));
                result.setStackTrace(failure.getStackTrace());
                for (Throwable suppressed : failure.getSuppressed())
                {
                    result.addSuppressed(suppressed);
                }
                return result;
            }
            return diagnose.call(failure, diagnostic);
        }
        catch (RuntimeException decorationFailure)
        {
            return failure;
        }
    }

    public IntentViolation missing(String operation, RuntimeKey<?> key, StageName stage, String boundary)
    {
        IntentViolation fallback = new IntentViolation("No intent is configured for event '" + key.key()
                + "'. See /Troubleshooting.md#intent-violation");
        try
        {
            return new IntentViolation(fallback.getMessage(), null,
                    build.call(DiagnosticCode.INTENT_MISSING, operation, null, key, stage, boundary, true));
        }
        catch (RuntimeException decorationFailure)
        {
            return fallback;
        }
    }

    public ExistentialExceptions aggregate(String operation, StageName stage, String boundary,
            List<ExistentialException> failures, boolean stopped)
    {
        ExistentialException[] multiple = failures.toArray(new ExistentialException[0]);
        try
        {
            Diagnostic diagnostic = build.aggregate(operation, stage, boundary, failures, stopped);
            return stage == StageName.VALIDATION ? ValidationStageExceptions.diagnosed(diagnostic, multiple)
                    : ExistentialExceptions.diagnosed(stage.label(), diagnostic, multiple);
        }
        catch (RuntimeException decorationFailure)
        {
            return stage == StageName.VALIDATION ? new ValidationStageExceptions(multiple)
                    : new ExistentialExceptions(stage.label(), multiple);
        }
    }

    /** Called only after the failed handle has actually become rollback-only. */
    public RuntimeException boundaryFailure(Throwable failure)
    {
        if (failure instanceof Error)
        {
            throw (Error) failure;
        }
        if (!(failure instanceof ExistentialException))
        {
            return (RuntimeException) failure;
        }
        ExistentialException original = (ExistentialException) failure;
        if (original.diagnostic() == null)
        {
            return original;
        }
        try
        {
            ExistentialException result = diagnose.boundary(original, build.rollbackOnly(original.diagnostic()));
            result.setStackTrace(original.getStackTrace());
            return result;
        }
        catch (RuntimeException decorationFailure)
        {
            return original;
        }
    }
}
