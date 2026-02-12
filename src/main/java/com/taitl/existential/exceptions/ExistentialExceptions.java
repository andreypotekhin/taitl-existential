package com.taitl.existential.exceptions;

public class ExistentialExceptions extends ExistentialException
{
    public ExistentialExceptions(String stage, ExistentialException... multiple)
    {
    }

    public ExistentialExceptions(String stage, String message, ExistentialException... multiple)
    {
        super(message);
    }
}
