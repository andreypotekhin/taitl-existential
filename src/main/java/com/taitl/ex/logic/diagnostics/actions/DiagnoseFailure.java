package com.taitl.ex.logic.diagnostics.actions;

import com.taitl.existential.diagnostics.*;
import com.taitl.ex.logic.diagnostics.data.*;
import com.taitl.existential.exceptions.*;

/** Copies library failures, retaining arbitrary supplied exceptions as causes rather than mutating them. */
public class DiagnoseFailure
{
    public ExistentialException call(RuntimeException failure, Diagnostic diagnostic)
    {
        Diagnostic previous =
                failure instanceof ExistentialException ? ((ExistentialException) failure).diagnostic() : null;
        String fallback = failure instanceof LibraryFailure && previous instanceof FailureSnapshot
                ? ((FailureSnapshot) previous).fallback
                : failure.getMessage();
        return copy(failure, new FailureSnapshot(diagnostic, fallback), fallback,
                failure instanceof LibraryFailure ? failure.getCause() : failure);
    }

    protected ExistentialException copy(RuntimeException failure, Diagnostic diagnostic, String fallback,
            Throwable cause)
    {
        ExistentialException result;
        if (failure instanceof MemoException && (diagnostic.code() == DiagnosticCode.MEMO_INVALID
                || diagnostic.code() == DiagnosticCode.MEMO_MISSING))
        {
            result = new MemoException(fallback, cause, diagnostic);
        }
        else if (failure.getClass() == ExistentialException.class
                && (diagnostic.code() == DiagnosticCode.INDEX_MAINTENANCE_FAILED
                        || diagnostic.code() == DiagnosticCode.CLEANUP_FAILED))
        {
            result = new ExistentialException(fallback, cause, diagnostic);
        }
        else if (failure instanceof IntentViolation)
        {
            result = new IntentViolation(fallback, cause, diagnostic);
        }
        else if (failure instanceof ConditionNotMetException)
        {
            result = new ConditionNotMetException(fallback, cause, diagnostic);
        }
        else if (failure instanceof InvariantViolation)
        {
            result = new InvariantViolation(fallback, cause, diagnostic);
        }
        else if (failure instanceof PredicateFailure)
        {
            result = new PredicateFailure(fallback, cause, diagnostic);
        }
        else
        {
            result = new EventHandlerException(fallback, cause, diagnostic);
        }
        result.setStackTrace(failure.getStackTrace());
        for (Throwable suppressed : failure.getSuppressed())
        {
            result.addSuppressed(suppressed);
        }
        return result;
    }

    public ExistentialException boundary(ExistentialException failure, Diagnostic diagnostic)
    {
        ExistentialException result;
        if (failure instanceof ValidationStageExceptions)
        {
            result = ValidationStageExceptions.diagnosed(diagnostic, ((ValidationStageExceptions) failure).multiple());
        }
        else if (failure instanceof ExistentialExceptions)
        {
            result = ExistentialExceptions.diagnosed(((ExistentialExceptions) failure).stage(), diagnostic,
                    ((ExistentialExceptions) failure).multiple());
        }
        else
        {
            String fallback = failure.diagnostic() instanceof FailureSnapshot
                    ? ((FailureSnapshot) failure.diagnostic()).fallback
                    : diagnostic.message();
            return copy(failure, diagnostic, fallback, failure.getCause());
        }
        java.util.Set<Throwable> existing = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        java.util.Collections.addAll(existing, result.getSuppressed());
        for (Throwable suppressed : failure.getSuppressed())
        {
            if (existing.add(suppressed))
            {
                result.addSuppressed(suppressed);
            }
        }
        return result;
    }
}
