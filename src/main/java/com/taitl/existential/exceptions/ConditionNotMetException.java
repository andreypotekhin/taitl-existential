package com.taitl.existential.exceptions;

/**
 * Signals that a configured rule or predicate failed its condition.
 *
 * Uses "The specified condition is not met" as the default message when none
 * is provided.
 */
public class ConditionNotMetException extends ExistentialException
{
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
