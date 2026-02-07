package com.taitl.existential.exceptions;

public class InvariantViolation extends ExistentialException
{
    public InvariantViolation()
    {
    }

    public InvariantViolation(String message)
    {
        super(message);
    }

    public InvariantViolation(Throwable cause)
    {
        super(cause);
    }

    public InvariantViolation(String message, Throwable cause)
    {
        super(message, cause);
    }
}
