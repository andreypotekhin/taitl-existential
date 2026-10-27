package com.taitl.existential.exceptions;

import com.taitl.existential.diagnostics.*;

/**
 * Signals that a requested entity or resource could not be found.
 */
public class NotFoundException extends ExistentialException
{
    /**
     * Creates a contextualized lookup failure with an original fallback message.
     *
     * @param message fallback detail message
     * @param cause original cause, possibly null
     * @param diagnostic detached structured snapshot, possibly null
     */
    public NotFoundException(String message, Throwable cause, Diagnostic diagnostic)
    {
        super(message, cause, diagnostic);
    }

    /**
     * Creates an exception with the default not-found message.
     */
    public NotFoundException()
    {
        super("Requested item was not found");
    }

    /**
     * Creates an exception with a custom message.
     * Uses the default not-found message when the provided message is null.
     *
     * @param message
     *            Detail message
     */
    public NotFoundException(String message)
    {
        super(message != null ? message : "Requested item was not found");
    }

    /**
     * Creates an exception with a cause and the default not-found message.
     *
     * @param cause
     *            Underlying cause
     */
    public NotFoundException(Throwable cause)
    {
        super("Requested item was not found", cause);
    }

    /**
     * Creates an exception with a custom message and cause.
     * Uses the default not-found message when the provided message is null.
     *
     * @param message
     *            Detail message
     * @param cause
     *            Underlying cause
     */
    public NotFoundException(String message, Throwable cause)
    {
        super(message != null ? message : "Requested item was not found", cause);
    }
}
