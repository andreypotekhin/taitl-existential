package com.taitl.existential.exceptions;

public class EventHandlerFailureException extends FailureException
{
    private static final long serialVersionUID = 1L;

    public EventHandlerFailureException()
    {
    }

    public EventHandlerFailureException(String message)
    {
        super(message);
    }

    public EventHandlerFailureException(Throwable cause)
    {
        super(cause);
    }

    public EventHandlerFailureException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
