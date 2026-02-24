package com.taitl.ex.common.helper;

import static com.taitl.existential.constants.Strings.*;

/**
 * Lightweight checks for method in-conditions.
 * Throws IllegalStateException if in-condition is not met.
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
            throw new IllegalStateException(String.format(FIELD_MUST_NOT_BE_NULL, objName));
        }

        return o;
    }

    /**
     * Throws IllegalStateException if any of the specified values is null.
     */
    public static void cool(Object o, String objName, Object... args)
    {
        ArgPairChecks.requireNonNullPairs(o,
                objName,
                ARGUMENT_MUST_NOT_BE_NULL,
                ARGUMENT_MUST_NOT_BE_NULL,
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
        ArgPairChecks.requireAllTrue(condition, message, messageArg -> {
            throw new IllegalStateException(messageArg);
        }, args);
    }

}
