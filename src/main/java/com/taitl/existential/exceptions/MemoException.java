package com.taitl.existential.exceptions;

import com.taitl.existential.diagnostics.*;

/**
 * Signals that transaction memo state is missing or invalid for memo-backed bi-event evaluation.
 */
public class MemoException extends ExistentialException
{
    /**
     * Creates a memo failure with detached diagnostic data and the original fallback message.
     *
     * @param message original fallback message
     * @param cause underlying cause, possibly null
     * @param diagnostic detached snapshot, possibly null
     */
    public MemoException(String message, Throwable cause, Diagnostic diagnostic)
    {
        super(message, cause, diagnostic);
    }

    public MemoException()
    {
        super("Transaction memo state is missing");
    }

    public MemoException(String message)
    {
        super(message != null ? message : "Transaction memo state is missing");
    }

    public MemoException(Throwable cause)
    {
        super("Transaction memo state is missing", cause);
    }

    public MemoException(String message, Throwable cause)
    {
        super(message != null ? message : "Transaction memo state is missing", cause);
    }
}
