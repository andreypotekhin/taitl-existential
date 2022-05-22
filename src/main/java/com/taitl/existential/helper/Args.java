package com.taitl.existential.helper;

/**
 * Lightweight checking/validations for method arguments.
 * Throws IllegalArgumentException if a condition is not met.
 *
 * This class just throws an exception. If more processing needed,
 * for instance, notifying an observer, consider Debug.require().
 *
 * @author Andrey Potekhin
 *
 * @see State
 */
public class Args
{
    protected static final String ARGUMENT_MUST_NOT_BE_NULL = "Argument '%s' must not be null";
    protected static final String ARGUMENT_ARRAY_MUST_BE_EVEN_LENGTH =
            "Argument '%s' must be of even length";

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
    public static void cool(Object... args)
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
}
