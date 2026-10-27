package com.taitl.existential.exceptions;

import com.taitl.existential.diagnostics.*;

/**
 * Signals a failed predicate evaluation inside a rule or constraint.
 */
public class PredicateFailure extends ExistentialException
{
    /**
     * Creates a contextualized failure without changing the underlying cause.
     *
     * @param message original fallback message
     * @param cause original cause, possibly null
     * @param diagnostic detached structured diagnostic
     */
    public PredicateFailure(String message, Throwable cause, Diagnostic diagnostic)
    {
        super(message, cause, diagnostic);
    }

    public static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#entity-quantifier-validation";

    /**
     * Creates a predicate failure without a message or cause.
     */
    public PredicateFailure()
    {
    }

    /**
     * Creates a predicate failure with a detail message.
     *
     * @param message
     *            Detail message
     */
    public PredicateFailure(String message)
    {
        super(message);
    }

    /**
     * Creates a predicate failure with a cause.
     *
     * @param cause
     *            Underlying cause
     */
    public PredicateFailure(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates a predicate failure with a detail message and cause.
     *
     * @param message
     *            Detail message
     * @param cause
     *            Underlying cause
     */
    public PredicateFailure(String message, Throwable cause)
    {
        super(message, cause);
    }
}
