package com.taitl.existential.helper;

import static com.taitl.existential.constants.Strings.*;

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
            throw new IllegalStateException(String.format(ARGUMENT_MUST_NOT_BE_NULL, objName));
        }
        for (int i = 0; i < args.length; i += 2)
        {
            if (args[i] == null)
            {
                throw new IllegalStateException(
                        String.format(ARGUMENT_MUST_NOT_BE_NULL, args[i + 1]));
            }
        }
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
        if (args.length % 2 != 0)
        {
            throw new IllegalArgumentException(
                    String.format(ARGUMENT_ARRAY_MUST_BE_EVEN_LENGTH, "args"));
        }
        if (!condition)
        {
            throw new IllegalStateException(message);
        }
        for (int i = 0; i < args.length; i += 2)
        {
            switch (args[i])
            {
            case Boolean b:
                if (!b)
                {
                    throw new IllegalStateException(String.valueOf(args[i + 1]));
                }
                break;
            default:
                throw new IllegalArgumentException(
                        String.format(ARGUMENT_MUST_BE_BOOLEAN, i));
            }
        }
    }

}
