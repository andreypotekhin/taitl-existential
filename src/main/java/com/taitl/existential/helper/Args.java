package com.taitl.existential.helper;

import static com.taitl.existential.constants.Strings.*;

/**
 * Lightweight checking/validations for method arguments.
 * Throws IllegalArgumentException if a condition is not met.
 *
 * @author Andrey Potekhin
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
    public static void cool(Object o, String argName)
    {
        if (o == null)
        {
            throw new IllegalArgumentException(String.format(ARGUMENT_MUST_NOT_BE_NULL, argName));
        }
    }

    /**
     * Throws IllegalArgumentException if method argument is null.
     */
    public static void cool(Object o, String argName, Object... args)
    {
        if (args == null)
        {
            throw new IllegalArgumentException(String.format(ARGUMENT_MUST_NOT_BE_NULL, "args"));
        }
        if (args.length % 2 != 0)
        {
            throw new IllegalArgumentException(
                    String.format(ARGUMENT_ARRAY_MUST_BE_EVEN_LENGTH, "args"));
        }
        if (o == null)
        {
            throw new IllegalArgumentException(String.format(ARGUMENT_MUST_NOT_BE_NULL, argName));
        }
        for (int i = 0; i < args.length; i += 2)
        {
            if (args[i] == null)
            {
                throw new IllegalArgumentException(
                        String.format(ARGUMENT_MUST_NOT_BE_NULL, args[i + 1]));
            }
        }
    }

    /**
     * Throws IllegalArgumentException if condition not met.
     */
    public static void require(boolean condition, String message)
    {
        if (!condition)
        {
            throw new IllegalArgumentException(message);
        }
    }

    public static void require(boolean condition, String message, Object... args)
    {
        if (args.length % 2 != 0)
        {
            throw new IllegalArgumentException(
                    String.format(ARGUMENT_ARRAY_MUST_BE_EVEN_LENGTH, "args"));
        }
        if (!condition)
        {
            throw new IllegalArgumentException(message);
        }
        for (int i = 0; i < args.length; i += 2)
        {
            switch (args[i])
            {
            case Boolean b:
                if (!b)
                {
                    throw new IllegalArgumentException(String.valueOf(args[i + 1]));
                }
                break;
            default:
                throw new IllegalArgumentException(
                        String.format(ARGUMENT_MUST_BE_BOOLEAN, i));
            }
        }
    }

}
