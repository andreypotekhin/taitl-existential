package com.taitl.existential.exceptions;

/**
 * Signals that a configured invariant has been violated during validation.
 */
public class InvariantViolation extends ExistentialException
{
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
