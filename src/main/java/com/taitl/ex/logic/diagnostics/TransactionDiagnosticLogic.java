package com.taitl.ex.logic.diagnostics;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.diagnostics.actions.*;
import com.taitl.existential.diagnostics.*;
import com.taitl.existential.exceptions.*;

/** Failure-only construction; guard callers pass detached facts without changing handle or registry state. */
public class TransactionDiagnosticLogic
{
    @Logic
    protected BuildTransactionDiagnostic build = Creator.create(BuildTransactionDiagnostic.class);

    public ExistentialException closed(String operation, String id, String action)
    {
        String verb = action != null ? action : "perform this operation";
        String fallback = String.format(
                "Cannot %s because the transaction is closed. Begin a new transaction for additional work. See %s",
                verb, "/Troubleshooting.md#transaction-closed");
        return contextualize(fallback, DiagnosticCode.TRANSACTION_CLOSED, operation, id, action);
    }

    public ExistentialException disposedView(String operation)
    {
        String fallback = "Managed index view is disposed. Begin a new transaction. "
                + "See /Troubleshooting.md#managed-index-maintenance";
        return contextualize(fallback, DiagnosticCode.TRANSACTION_CLOSED, operation, null, "access managed indexes");
    }

    public ExistentialException rollbackOnly(String operation, String id, String action)
    {
        String fallback = String.format(
                "Cannot %s because evaluation or index maintenance failed. Roll back this transaction or close it before "
                        + "beginning another transaction. See %s",
                action, "/Troubleshooting.md#transaction-closed");
        return contextualize(fallback, DiagnosticCode.TRANSACTION_ROLLBACK_ONLY, operation, id, action);
    }

    public NotFoundException missing(String id)
    {
        String fallback = String.format("Transaction not found, id=%s. See %s",
                id, "/Troubleshooting.md#transaction-not-found");
        return (NotFoundException) contextualize(fallback, DiagnosticCode.TRANSACTION_NOT_FOUND, null, id, null);
    }

    protected ExistentialException contextualize(String fallback, DiagnosticCode code,
            String operation, String id, String action)
    {
        Diagnostic diagnostic;
        try
        {
            diagnostic = build.call(code, operation, id, action);
        }
        catch (RuntimeException decorationFailure)
        {
            diagnostic = null;
        }
        if (code == DiagnosticCode.TRANSACTION_NOT_FOUND)
        {
            return diagnostic == null ? new NotFoundException(fallback)
                    : new NotFoundException(fallback, null, diagnostic);
        }
        return diagnostic == null ? new ExistentialException(fallback)
                : new ExistentialException(fallback, null, diagnostic);
    }
}
