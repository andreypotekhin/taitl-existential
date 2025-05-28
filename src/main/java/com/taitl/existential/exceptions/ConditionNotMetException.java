package com.taitl.existential.exceptions;

import static com.taitl.existential.constants.Strings.CONDITION_NOT_MET;

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
        super(cause);
    }

    public ConditionNotMetException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
