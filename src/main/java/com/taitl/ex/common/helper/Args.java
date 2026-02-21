package com.taitl.ex.common.helper;

import static com.taitl.existential.constants.Strings.*;

/**
 * Lightweight checking/validations for method arguments.
 * Throws IllegalArgumentException if a condition is not met.
 *
 * @see State
 * @see Outcome
 */
public class Args
{
    /**
     * Protected constructor for an utility class.
     */
    protected Args()
    {
    }

    /**
     * Throws IllegalArgumentException if method argument is null.
     */
    public static void sane(Object o, String argName)
    {
        if (o == null)
        {
            throw new IllegalArgumentException(String.format(ARGUMENT_MUST_NOT_BE_NULL, argName));
        }
    }

    /**
     * Throws IllegalArgumentException if method argument is null.
     */
    public static void sane(Object o, String argName, Object... args)
    {
        ArgPairs.requireEvenArgs(args);
        if (o == null)
        {
            throw new IllegalArgumentException(String.format(ARGUMENT_MUST_NOT_BE_NULL, argName));
        }
        for (int i = 0; i < args.length; i += 2)
        {
            if (args[i] == null)
            {
                throw new IllegalArgumentException(String.format(ARGUMENT_MUST_NOT_BE_NULL, args[i + 1]));
            }
        }
    }

    /**
     * Throws IllegalArgumentException if condition not met.
     */
    public static void check(boolean condition, String message)
    {
        if (!condition)
        {
            throw new IllegalArgumentException(message);
        }
    }

    public static void check(boolean condition, String message, Object... args)
    {
        ArgPairs.requireEvenArgs(args);
        if (!condition)
        {
            throw new IllegalArgumentException(message);
        }
        ArgPairs.requireBooleanPairs(args);
        for (int i = 0; i < args.length; i += 2)
        {
            if (!((Boolean) args[i]))
            {
                throw new IllegalArgumentException(String.valueOf(args[i + 1]));
            }
        }
    }

}
