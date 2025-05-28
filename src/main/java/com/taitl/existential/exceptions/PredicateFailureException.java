package com.taitl.existential.exceptions;

public class PredicateFailureException extends ExistentialException
{
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
