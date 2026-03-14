package com.taitl.ex.common.helper;

/**
 * Lightweight checks for method in-conditions.
 * Throws IllegalStateException if in-condition is not met.
 *
 * @see Args
 * @see Outcome
 */
public class State
{
    /**
     * Protected constructor for an utility class.
     */
    protected State()
    {
    }

    /**
     * Throws IllegalStateException if specified value is null.
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
            throw new IllegalStateException(String.format("Field '%s' must not be null", objName));
        }

        return o;
    }

    /**
     * Throws IllegalStateException if any of the specified values is null.
     */
    public static void cool(Object o, String objName, Object... args)
    {
        Args.requireNonNullPairs(o,
                objName,
                "Argument '%s' must not be null",
                "Argument '%s' must not be null",
                message -> {
                    throw new IllegalStateException(message);
                },
                args);
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

    /**
     * Throws IllegalStateException if any of the specified conditions is not met.
     */
    public static void verify(boolean condition, String message, Object... args)
    {
        Args.requireAllTrue(condition, message, messageArg -> {
            throw new IllegalStateException(messageArg);
        }, args);
    }

}
