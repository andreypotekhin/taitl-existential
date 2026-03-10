package com.taitl.ex.common.helper;

/**
 * Shared checks for vararg pairs with caller-provided exception mapping.
 */
public class ArgPairChecks
{
    @FunctionalInterface
    public interface Thrower
    {
        void raise(String message);
    }

    /**
     * Protected constructor for an utility class.
     */
    protected ArgPairChecks()
    {
    }

    /**
     * Throws via provided thrower if first or any pair element is null.
     */
    public static void requireNonNullPairs(Object first,
            String firstName,
            String firstMessage,
            String pairMessage,
            Thrower thrower,
            Object... args)
    {
        Args.requireEvenArgs(args);
        if (first == null)
        {
            thrower.raise(String.format(firstMessage, firstName));
        }
        for (int i = 0; i < args.length; i += 2)
        {
            if (args[i] == null)
            {
                thrower.raise(String.format(pairMessage, args[i + 1]));
            }
        }
    }

    /**
     * Throws via provided thrower if any boolean condition in pairs is false.
     */
    public static void requireAllTrue(Thrower thrower, Object... args)
    {
        requireAllTrue(true, "", thrower, args);
    }

    /**
     * Throws via provided thrower if condition or any boolean pair is false.
     * Uses IllegalArgumentException for malformed pair inputs.
     */
    public static void requireAllTrue(boolean condition,
            String message,
            Thrower thrower,
            Object... args)
    {
        Args.requireEvenArgs(args);
        if (!condition)
        {
            thrower.raise(message);
        }
        for (int i = 0; i < args.length; i += 2)
        {
            if (!(args[i] instanceof Boolean))
            {
                throw new IllegalArgumentException(String.format("Argument '%s' must be boolean", i));
            }
            if (!((Boolean) args[i]))
            {
                thrower.raise(String.valueOf(args[i + 1]));
            }
        }
    }
}
