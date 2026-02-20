package com.taitl.ex.common.helper;

/**
 * Argument validations for a pair of values.
 */
public class PairArgs
{
    /**
     * Protected constructor for an utility class.
     */
    protected PairArgs()
    {
    }

    /**
     * Throws IllegalArgumentException if either argument is null.
     */
    public static void requireBothNonNull(Object first, Object second, String firstMessage, String secondMessage)
    {
        if (first == null)
        {
            throw new IllegalArgumentException(firstMessage);
        }
        if (second == null)
        {
            throw new IllegalArgumentException(secondMessage);
        }
    }

    /**
     * Throws IllegalArgumentException if both arguments are null.
     */
    public static void requireNotBothNull(Object first, Object second, String message)
    {
        if (first == null && second == null)
        {
            throw new IllegalArgumentException(message);
        }
    }
}
