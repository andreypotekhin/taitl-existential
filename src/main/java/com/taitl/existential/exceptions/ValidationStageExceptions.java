package com.taitl.existential.exceptions;

public class ValidationStageExceptions extends ExistentialExceptions
{
    private static final String STAGE = "Validation";

    public ValidationStageExceptions(ExistentialException... multiple)
    {
        super(STAGE, multiple);
    }

    public ValidationStageExceptions(String message, ExistentialException... multiple)
    {
        super(STAGE, message, multiple);
    }
}
