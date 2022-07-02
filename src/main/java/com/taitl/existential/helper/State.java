package com.taitl.existential.helper;

/**
 * Lightweight checks for method in-conditions.
 * Throws IllegalStateException if in-condition is not met.
 *
 * @author Andrey Potekhin
 *
 * @see Args
 * @see Outcome
 */
public class State
{
    protected static final String FIELD_MUST_NOT_BE_NULL = "Field '%s' must not be null";

    /**
     * Protected constructor for an utility class.
     */
    protected State()
    {
    }

    /**
     * Throws IllegalStateException if method outcome is null.
     *
     * Example:
     * return State.cool(result, 'result')
     *
     * @return Returns argument 'o', for fluency
     */
    public static Object cool(Object o, String objName)
    {
        if (o == null)
        {
            throw new IllegalStateException(String.format(FIELD_MUST_NOT_BE_NULL, objName));
        }

        return o;
    }

    /**
     * Throws IllegalStateException if condition is not met.
     */
    public static void verify(boolean condition, String message)
    {
        if (!condition)
        {
            throw new IllegalStateException(message);
        }
    }
}
