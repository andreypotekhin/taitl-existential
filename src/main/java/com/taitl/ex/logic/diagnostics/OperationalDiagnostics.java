package com.taitl.ex.logic.diagnostics;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.diagnostics.actions.*;
import com.taitl.existential.diagnostics.*;
import com.taitl.existential.exceptions.*;

/** Failure-only factories preserve stock catch types and avoid a Throwable-to-metadata registry. */
public class OperationalDiagnostics
{
    public static Diagnostic snapshot(DiagnosticCode code, String detail, String operation)
    {
        try
        {
            return Creator.singleton(BuildOperationalDiagnostic.class).call(code, detail, operation);
        }
        catch (RuntimeException decorationFailure)
        {
            return null;
        }
    }

    public static String message(DiagnosticCode code, String detail)
    {
        try
        {
            Diagnostic diagnostic = snapshot(code, detail, null);
            return diagnostic == null ? detail : Creator.singleton(DiagnosticLogic.class).render(diagnostic);
        }
        catch (RuntimeException formattingFailure)
        {
            return detail;
        }
    }

    public static IllegalStateException state(DiagnosticCode code, String detail)
    {
        return new IllegalStateException(message(code, detail));
    }

    public static IllegalStateException state(DiagnosticCode code, String detail, Throwable cause)
    {
        return new IllegalStateException(message(code, detail), cause);
    }

    public static IllegalStateException configuration(IllegalStateException original)
    {
        IllegalStateException result =
                state(DiagnosticCode.CONFIGURATION_INVALID, original.getMessage(), original.getCause());
        result.setStackTrace(original.getStackTrace());
        for (Throwable suppressed : original.getSuppressed())
        {
            result.addSuppressed(suppressed);
        }
        return result;
    }

    public static IllegalArgumentException argument(String detail)
    {
        return new IllegalArgumentException(message(DiagnosticCode.CONFIGURATION_INVALID, detail));
    }

    public static void requireConfiguration(boolean valid, String detail)
    {
        if (!valid)
        {
            throw state(DiagnosticCode.CONFIGURATION_INVALID, detail);
        }
    }

    public static void requireMutable(boolean valid, String detail)
    {
        if (!valid)
        {
            throw state(DiagnosticCode.CONFIGURATION_FROZEN, detail);
        }
    }

    public static MemoException memo(DiagnosticCode code, String detail, String operation)
    {
        return new MemoException(detail, null, snapshot(code, detail, operation));
    }

    public static ExistentialException failure(DiagnosticCode code, String detail, String operation, Throwable cause)
    {
        return new ExistentialException(detail, cause, snapshot(code, detail, operation));
    }
}
