package com.taitl.existential.exceptions;

import com.taitl.existential.diagnostics.*;

/**
 * Signals that a runtime event is not authorized by configured intents.
 */
public class IntentViolation extends ExistentialException
{
    /**
     * Creates a contextualized failure without changing the underlying cause.
     *
     * @param message original fallback message
     * @param cause original cause, possibly null
     * @param diagnostic detached structured diagnostic
     */
    public IntentViolation(String message, Throwable cause, Diagnostic diagnostic)
    {
        super(message, cause, diagnostic);
    }

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
