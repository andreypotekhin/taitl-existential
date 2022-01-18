package com.taitl.existential.exceptions;

public class ExistentialException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ExistentialException()
    {
    }

    public ExistentialException(String message)
    {
        super(message);
    }

    public ExistentialException(Throwable cause)
    {
        super(cause);
    }

    public ExistentialException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
