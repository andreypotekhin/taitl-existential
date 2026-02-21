package com.taitl.ex.common.helper;

import static com.taitl.existential.constants.Strings.*;

/**
 * Validation helpers for vararg pairs.
 */
public class ArgPairs
{
    /**
     * Protected constructor for an utility class.
     */
    protected ArgPairs()
    {
    }

    /**
     * Throws IllegalArgumentException if args is null or length is not even.
     */
    public static void requireEvenArgs(Object... args)
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
    }

    /**
     * Throws IllegalArgumentException if any even index is not a Boolean.
     */
    public static void requireBooleanPairs(Object... args)
    {
        for (int i = 0; i < args.length; i += 2)
        {
            if (!(args[i] instanceof Boolean))
            {
                throw new IllegalArgumentException(String.format(ARGUMENT_MUST_BE_BOOLEAN, i));
            }
        }
    }
}
