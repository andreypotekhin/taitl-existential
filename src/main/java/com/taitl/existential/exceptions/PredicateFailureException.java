package com.taitl.existential.exceptions;

public class PredicateFailureException extends FailureException
{
    private static final long serialVersionUID = 1L;

    public PredicateFailureException()
    {
    }

    public PredicateFailureException(String message)
    {
        super(message);
    }

    public PredicateFailureException(Throwable cause)
    {
        super(cause);
    }

    public PredicateFailureException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
