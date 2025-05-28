package com.taitl.existential.exceptions;

public class EventHandlerExecutionException extends ExistentialException
{
    public EventHandlerExecutionException()
    {
    }

    public EventHandlerExecutionException(String message)
    {
        super(message);
    }

    public EventHandlerExecutionException(Throwable cause)
    {
        super(cause);
    }

    public EventHandlerExecutionException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
