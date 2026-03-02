package com.taitl.existential.configs;

/**
 * Names of execution stages for configured rules.
 */
public enum StageName
{
    PRECONDITION, IMMEDIATE, VALIDATION;

    public String label()
    {
        String lower = name().toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
