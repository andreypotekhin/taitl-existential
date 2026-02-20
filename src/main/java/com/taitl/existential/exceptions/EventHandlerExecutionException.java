package com.taitl.existential.exceptions;

import static com.taitl.existential.constants.Strings.*;

public class EventHandlerExecutionException extends ExistentialException
{
    public EventHandlerExecutionException()
    {
        super(EVENT_HANDLER_EXECUTION_FAILED);
    }

    public EventHandlerExecutionException(String message)
    {
        super(message != null ? message : EVENT_HANDLER_EXECUTION_FAILED);
    }

    public EventHandlerExecutionException(Throwable cause)
    {
        super(EVENT_HANDLER_EXECUTION_FAILED, cause);
    }

    public EventHandlerExecutionException(String message, Throwable cause)
    {
        super(message != null ? message : EVENT_HANDLER_EXECUTION_FAILED, cause);
    }
}
