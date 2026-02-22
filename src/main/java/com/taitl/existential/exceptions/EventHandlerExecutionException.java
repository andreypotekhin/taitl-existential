package com.taitl.existential.exceptions;

import static com.taitl.existential.constants.Strings.*;

/**
 * Signals that an event handler failed during execution.
 * Defaults to {@link com.taitl.existential.constants.Strings#EVENT_HANDLER_EXECUTION_FAILED}
 * when no explicit message is provided.
 */
public class EventHandlerExecutionException extends ExistentialException
{
    /**
     * Creates an exception with the default message.
     */
    public EventHandlerExecutionException()
    {
        super(EVENT_HANDLER_EXECUTION_FAILED);
    }

    /**
     * Creates an exception with a message, falling back to the default message when null.
     *
     * @param message Message to use or null to use the default message.
     */
    public EventHandlerExecutionException(String message)
    {
        super(message != null ? message : EVENT_HANDLER_EXECUTION_FAILED);
    }

    /**
     * Creates an exception with the default message and a cause.
     *
     * @param cause Original cause for the failure.
     */
    public EventHandlerExecutionException(Throwable cause)
    {
        super(EVENT_HANDLER_EXECUTION_FAILED, cause);
    }

    /**
     * Creates an exception with a message and a cause.
     *
     * @param message Message to use or null to use the default message.
     * @param cause   Original cause for the failure.
     */
    public EventHandlerExecutionException(String message, Throwable cause)
    {
        super(message != null ? message : EVENT_HANDLER_EXECUTION_FAILED, cause);
    }
}
