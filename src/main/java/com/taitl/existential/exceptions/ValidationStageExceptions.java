package com.taitl.existential.exceptions;

import com.taitl.existential.diagnostics.*;

/**
 * Groups validation stage exceptions to preserve stage context for diagnostics.
 */
public class ValidationStageExceptions extends ExistentialExceptions
{
    protected ValidationStageExceptions(String message, Diagnostic diagnostic, ExistentialException... multiple)
    {
        super("Validation", message, diagnostic, multiple);
    }

    /**
     * Creates a diagnosed validation aggregate.
     *
     * @param diagnostic detached aggregate diagnostic
     * @param multiple sibling failures in evaluation order
     * @return diagnosed validation aggregate
     */
    public static ValidationStageExceptions diagnosed(Diagnostic diagnostic, ExistentialException... multiple)
    {
        return new ValidationStageExceptions(null, diagnostic, multiple);
    }

    /**
     * Creates a validation-stage exception set.
     *
     * @param multiple
     *            Exceptions raised during validation
     */
    public ValidationStageExceptions(ExistentialException... multiple)
    {
        super("Validation", multiple);
    }

    /**
     * Creates a validation-stage exception set with a custom message.
     *
     * @param message
     *            Detail message
     * @param multiple
     *            Exceptions raised during validation
     */
    public ValidationStageExceptions(String message, ExistentialException... multiple)
    {
        super("Validation", message, multiple);
    }
}
