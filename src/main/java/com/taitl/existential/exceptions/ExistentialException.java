package com.taitl.existential.exceptions;

import com.taitl.existential.diagnostics.*;
import com.taitl.ex.logic.diagnostics.*;
import com.taitl.ex.common.creator.*;

/**
 * Base unchecked exception for failures originating from the Existential runtime.
 *
 * Use this type as the common catch point for library-specific errors,
 * while more specialized subclasses provide additional intent.
 */
public class ExistentialException extends RuntimeException
{
    private Diagnostic diagnostic;
    private volatile String rendered;
    private volatile boolean renderedReady;

    /**
     * Creates a failure with detached diagnostic data and an original fallback message.
     *
     * @param message fallback detail message
     * @param cause underlying cause, possibly null
     * @param diagnostic structured snapshot, possibly null
     */
    public ExistentialException(String message, Throwable cause, Diagnostic diagnostic)
    {
        super(message, cause);
        this.diagnostic = diagnostic;
    }

    /**
     * Returns this failure's detached structured data.
     *
     * @return diagnostic snapshot, or null for an undecorated failure
     */
    public Diagnostic diagnostic()
    {
        return diagnostic;
    }

    /**
     * Renders structured data once, falling back to the original message if formatting fails.
     *
     * @return detailed diagnostic or the original plain message
     */
    public String getMessage()
    {
        if (diagnostic == null)
        {
            return super.getMessage();
        }
        if (!renderedReady)
        {
            synchronized (this)
            {
                if (!renderedReady)
                {
                    try
                    {
                        rendered = Creator.singleton(DiagnosticLogic.class).render(diagnostic);
                    }
                    catch (RuntimeException failure)
                    {
                        rendered = super.getMessage();
                    }
                    renderedReady = true;
                }
            }
        }
        return rendered;
    }

    public ExistentialException()
    {
    }

    public ExistentialException(String message)
    {
        super(message);
    }

    public ExistentialException(Throwable cause)
    {
        super(cause);
    }

    public ExistentialException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
