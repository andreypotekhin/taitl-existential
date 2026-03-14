package com.taitl.ex.common.helper;

/**
 * Lightweight checks for method post-conditions.
 * Throws RuntimeException if post-condition is not met.
 *
 * @see Args
 * @see State
 */
public class Outcome
{
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
            throw new RuntimeException(String.format("Value '%s' must not be null", objName));
        }

        return o;
    }

    /**
     * Throws RuntimeException if any even argument is null.
     */
    public static void cool(Object o, String objName, Object... args)
    {
        Args.requireNonNullPairs(o,
                objName,
                "Value '%s' must not be null",
                "Value '%s' must not be null",
                message -> {
                    throw new RuntimeException(message);
                },
                args);
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

    /**
     * Throws RuntimeException if any of the specified conditions is not met.
     */
    public static void verify(boolean condition, String message, Object... args)
    {
        Args.requireAllTrue(condition, message, messageArg -> {
            throw new RuntimeException(messageArg);
        }, args);
    }
}
