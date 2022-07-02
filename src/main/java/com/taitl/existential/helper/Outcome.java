package com.taitl.existential.helper;

/**
 * Lightweight checks for method post-conditions.
 * Throws RuntimeException if post-condition is not met.
 *
 * @author Andrey Potekhin
 *
 * @see Args
 * @see State
 */
public class Outcome
{
    protected static final String VALUE_MUST_NOT_BE_NULL = "Value '%s' must not be null";

    /**
     * Protected constructor for an utility class.
     */
    protected Outcome()
    {
    }

    /**
     * Throws RuntimeException if method outcome is null.
     *
     * Example:
     * return Outcome.cool(result, 'result')
     *
     * @return Returns argument 'o', for fluency
     */
    public static Object cool(Object o, String objName)
    {
        if (o == null)
        {
            throw new RuntimeException(String.format(VALUE_MUST_NOT_BE_NULL, objName));
        }

        return o;
    }

    /**
     * Throws RuntimeException if condition is not met.
     */
    public static void verify(boolean condition, String message)
    {
        if (!condition)
        {
            throw new RuntimeException(message);
        }
    }
}
