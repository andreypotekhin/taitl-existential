package com.taitl.existential.helper;

import static com.taitl.existential.constants.Strings.*;

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
        if (args.length % 2 != 0)
        {
            throw new IllegalArgumentException(
                    String.format(ARGUMENT_ARRAY_MUST_BE_EVEN_LENGTH, "args"));
        }
        if (!condition)
        {
            throw new RuntimeException(message);
        }
        for (int i = 0; i < args.length; i += 2)
        {
            switch (args[i])
            {
            case Boolean b:
                if (!b)
                {
                    throw new RuntimeException(String.valueOf(args[i + 1]));
                }
                break;
            default:
                throw new IllegalArgumentException(
                        String.format(ARGUMENT_MUST_BE_BOOLEAN, i));
            }
        }
    }
}
