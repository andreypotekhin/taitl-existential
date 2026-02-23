package com.taitl.existential.exceptions;

import static com.taitl.existential.constants.Strings.*;

/**
 * Signals that a requested entity or resource could not be found.
 */
public class NotFoundException extends ExistentialException
{
    /**
     * Creates an exception with the default not-found message.
     */
    public NotFoundException()
    {
        super(NOT_FOUND);
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
        super(message != null ? message : NOT_FOUND);
    }

    /**
     * Creates an exception with a cause and the default not-found message.
     *
     * @param cause
     *            Underlying cause
     */
    public NotFoundException(Throwable cause)
    {
        super(NOT_FOUND, cause);
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
        super(message != null ? message : NOT_FOUND, cause);
    }
}
