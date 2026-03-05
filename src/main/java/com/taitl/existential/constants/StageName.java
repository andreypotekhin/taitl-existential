package com.taitl.existential.constants;

/**
 * Names of execution stages for configured rules.
 */
public enum StageName
{
    BEGIN, IMMEDIATE, COMMIT, CHECKPOINT, ROLLBACK, VALIDATION;

    public String label()
    {
        String lower = name().toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
