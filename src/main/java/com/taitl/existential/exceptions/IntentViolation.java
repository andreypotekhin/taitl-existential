package com.taitl.existential.exceptions;

/**
 * Signals that a runtime event is not authorized by configured intents.
 */
public class IntentViolation extends ExistentialException
{
    public IntentViolation()
    {
    }

    public IntentViolation(String message)
    {
        super(message);
    }

    public IntentViolation(Throwable cause)
    {
        super(cause);
    }

    public IntentViolation(String message, Throwable cause)
    {
        super(message, cause);
    }
}
