package com.taitl.existential.exceptions;

/**
 * Groups multiple {@link ExistentialException} instances under a single error.
 *
 * Intended for stages that may produce more than one failure, while still
 * reporting a consolidated exception to the caller.
 */
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
