package com.taitl.existential.exceptions;

/**
 * Signals that an event handler failed during execution.
 * Defaults to "Event handler execution failed" when no explicit message is
 * provided.
 */
public class EventHandlerException extends ExistentialException
{
    /**
     * Creates an exception with the default message.
     */
    public EventHandlerException()
    {
        super("Event handler execution failed");
    }

    /**
     * Creates an exception with a message, falling back to the default message when null.
     *
     * @param message Message to use or null to use the default message.
     */
    public EventHandlerException(String message)
    {
        super(message != null ? message : "Event handler execution failed");
    }

    /**
     * Creates an exception with the default message and a cause.
     *
     * @param cause Original cause for the failure.
     */
    public EventHandlerException(Throwable cause)
    {
        super("Event handler execution failed", cause);
    }

    /**
     * Creates an exception with a message and a cause.
     *
     * @param message Message to use or null to use the default message.
     * @param cause   Original cause for the failure.
     */
    public EventHandlerException(String message, Throwable cause)
    {
        super(message != null ? message : "Event handler execution failed", cause);
    }
}
