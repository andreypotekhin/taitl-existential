package com.taitl.ex.common.helper;

import static com.taitl.existential.constants.Strings.*;

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
            throw new RuntimeException(String.format(VALUE_MUST_NOT_BE_NULL, objName));
        }

        return o;
    }

    /**
     * Throws RuntimeException if any even argument is null.
     */
    public static void cool(Object o, String objName, Object... args)
    {
        ArgPairs.requireEvenArgs(args);
        if (o == null)
        {
            throw new RuntimeException(String.format(VALUE_MUST_NOT_BE_NULL, objName));
        }
        for (int i = 0; i < args.length; i += 2)
        {
            if (args[i] == null)
            {
                throw new RuntimeException(
                        String.format(VALUE_MUST_NOT_BE_NULL, args[i + 1]));
            }
        }
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
        ArgPairs.requireEvenArgs(args);
        if (!condition)
        {
            throw new RuntimeException(message);
        }
        ArgPairs.requireBooleanPairs(args);
        for (int i = 0; i < args.length; i += 2)
        {
            if (!((Boolean) args[i]))
            {
                throw new RuntimeException(String.valueOf(args[i + 1]));
            }
        }
    }
}
