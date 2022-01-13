package com.taitl.existential.exceptions;

public class FailureException extends Exception
{
    private static final long serialVersionUID = 1L;

    public FailureException()
    {
    }

    public FailureException(String message)
    {
        super(message);
    }

    public FailureException(Throwable cause)
    {
        super(cause);
    }

    public FailureException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
