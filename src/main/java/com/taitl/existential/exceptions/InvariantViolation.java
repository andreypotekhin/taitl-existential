package com.taitl.existential.exceptions;

import com.taitl.existential.diagnostics.*;

/**
 * Signals that a configured invariant has been violated during validation.
 */
public class InvariantViolation extends ExistentialException
{
    /**
     * Creates a contextualized failure without changing the underlying cause.
     *
     * @param message original fallback message
     * @param cause original cause, possibly null
     * @param diagnostic detached structured diagnostic
     */
    public InvariantViolation(String message, Throwable cause, Diagnostic diagnostic)
    {
        super(message, cause, diagnostic);
    }

    /**
     * Creates an invariant violation without a message.
     */
    public InvariantViolation()
    {
    }

    /**
     * Creates an invariant violation with a message.
     *
     * @param message Message describing the violation.
     */
    public InvariantViolation(String message)
    {
        super(message);
    }

    /**
     * Creates an invariant violation with a cause.
     *
     * @param cause Original cause for the violation.
     */
    public InvariantViolation(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates an invariant violation with a message and a cause.
     *
     * @param message Message describing the violation.
     * @param cause   Original cause for the violation.
     */
    public InvariantViolation(String message, Throwable cause)
    {
        super(message, cause);
    }
}
