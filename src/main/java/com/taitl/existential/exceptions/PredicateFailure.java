package com.taitl.existential.exceptions;

public class PredicateFailure extends ExistentialException
{
    public PredicateFailure()
    {
    }

    public PredicateFailure(String message)
    {
        super(message);
    }

    public PredicateFailure(Throwable cause)
    {
        super(cause);
    }

    public PredicateFailure(String message, Throwable cause)
    {
        super(message, cause);
    }
}
