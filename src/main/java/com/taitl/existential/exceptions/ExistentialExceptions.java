package com.taitl.existential.exceptions;

/**
 * Groups multiple {@link ExistentialException} instances under a single error.
 *
 * Intended for stages that may produce more than one failure, while still
 * reporting a consolidated exception to the caller.
 */
public class ExistentialExceptions extends ExistentialException
{
    private final String stage;
    private final ExistentialException[] multiple;

    public ExistentialExceptions(String stage, ExistentialException... multiple)
    {
        this(stage, null, multiple);
    }

    public ExistentialExceptions(String stage, String message, ExistentialException... multiple)
    {
        super(resolveMessage(stage, message, multiple), resolveCause(multiple));
        this.stage = stage;
        this.multiple = multiple != null ? multiple.clone() : new ExistentialException[] {};
        addSuppressedExceptions(this.multiple);
    }

    public String stage()
    {
        return stage;
    }

    public ExistentialException[] multiple()
    {
        return multiple.clone();
    }

    private static String resolveMessage(String stage, String message, ExistentialException... multiple)
    {
        if (message != null)
        {
            return message;
        }

        int count = countNonNull(multiple);
        String resolvedStage = stage != null ? stage : "Unknown";
        String plural = count == 1 ? "" : "s";
        return resolvedStage + " stage failed with " + count + " error" + plural + ".";
    }

    private static Throwable resolveCause(ExistentialException... multiple)
    {
        if (multiple == null)
        {
            return null;
        }

        for (ExistentialException exception : multiple)
        {
            if (exception != null)
            {
                return exception;
            }
        }

        return null;
    }

    private static int countNonNull(ExistentialException... multiple)
    {
        if (multiple == null)
        {
            return 0;
        }

        int count = 0;
        for (ExistentialException exception : multiple)
        {
            if (exception != null)
            {
                count++;
            }
        }
        return count;
    }

    private void addSuppressedExceptions(ExistentialException... multiple)
    {
        if (multiple == null)
        {
            return;
        }

        for (ExistentialException exception : multiple)
        {
            if (exception != null && exception != getCause())
            {
                addSuppressed(exception);
            }
        }
    }
}
