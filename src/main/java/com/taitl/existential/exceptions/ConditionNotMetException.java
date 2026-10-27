package com.taitl.existential.exceptions;

import com.taitl.existential.diagnostics.*;

/**
 * Signals that a configured rule or predicate failed its condition.
 *
 * Uses "The specified condition is not met" as the default message when none
 * is provided.
 */
public class ConditionNotMetException extends ExistentialException
{
    /**
     * Creates a contextualized failure without changing the underlying cause.
     *
     * @param message original fallback message
     * @param cause original cause, possibly null
     * @param diagnostic detached structured diagnostic
     */
    public ConditionNotMetException(String message, Throwable cause, Diagnostic diagnostic)
    {
        super(message, cause, diagnostic);
    }

    public ConditionNotMetException()
    {
        super("The specified condition is not met");
    }

    public ConditionNotMetException(String message)
    {
        super(message != null ? message : "The specified condition is not met");
    }

    public ConditionNotMetException(Throwable cause)
    {
        super("The specified condition is not met", cause);
    }

    public ConditionNotMetException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
