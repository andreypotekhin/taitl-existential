package com.taitl.existential.exceptions;

/**
 * Signals that transaction memo state is missing or invalid for memo-backed bi-event evaluation.
 */
public class MemoException extends ExistentialException
{
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
