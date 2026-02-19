package com.taitl.existential.exceptions;

/**
 * Base exception for failures originating from the Existential runtime.
 *
 * Use this type as the common catch point for library-specific errors,
 * while more specialized subclasses provide additional intent.
 */
public class ExistentialException extends Exception
{
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
