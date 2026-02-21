package com.taitl.existential.exceptions;

import static com.taitl.existential.constants.Strings.CONDITION_NOT_MET;

/**
 * Signals that a configured rule or predicate failed its condition.
 *
 * Uses {@link com.taitl.existential.constants.Strings#CONDITION_NOT_MET} as the
 * default message when none is provided.
 */
public class ConditionNotMetException extends ExistentialException
{
    public ConditionNotMetException()
    {
        super(CONDITION_NOT_MET);
    }

    public ConditionNotMetException(String message)
    {
        super(message != null ? message : CONDITION_NOT_MET);
    }

    public ConditionNotMetException(Throwable cause)
    {
        super(CONDITION_NOT_MET, cause);
    }

    public ConditionNotMetException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
