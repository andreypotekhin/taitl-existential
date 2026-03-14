package com.taitl.existential.exceptions;

/**
 * Groups validation stage exceptions to preserve stage context for diagnostics.
 */
public class ValidationStageExceptions extends ExistentialExceptions
{
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
