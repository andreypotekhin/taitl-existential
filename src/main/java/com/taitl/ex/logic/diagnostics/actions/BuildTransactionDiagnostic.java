package com.taitl.ex.logic.diagnostics.actions;

import com.taitl.ex.logic.diagnostics.data.*;
import com.taitl.existential.diagnostics.*;
import java.util.*;

/** Copies only known admission/state facts; missing registry entries cannot supply a former operation. */
public class BuildTransactionDiagnostic
{
    public Diagnostic call(DiagnosticCode code, String operation, String id, String action)
    {
        String message;
        String remedy;
        switch (code)
        {
        case TRANSACTION_CLOSED:
            message = "Transaction is closed";
            remedy = "Begin a new transaction for additional work";
            break;
        case TRANSACTION_ROLLBACK_ONLY:
            message = "Transaction is rollback-only";
            remedy = "Roll back or close this transaction before beginning another transaction";
            break;
        case TRANSACTION_NOT_FOUND:
            message = "Transaction not found";
            remedy = "Check the active transaction ID; completed or detached IDs cannot be reused";
            break;
        default:
            throw new IllegalArgumentException("Unsupported transaction diagnostic code: " + code);
        }
        List<String> notes = new ArrayList<>();
        if (action != null)
        {
            notes.add("Requested action: " + action);
        }
        if (id != null)
        {
            notes.add("Transaction ID: " + id);
        }
        return new DiagnosticSnapshot(code, message, operation, null, null, false,
                null, null, null, null, null, boundary(action), false, notes, List.of(remedy),
                "/Troubleshooting.md#diagnostic-" + code.id().toLowerCase(Locale.ROOT), List.of());
    }

    protected String boundary(String action)
    {
        if ("checkpoint".equals(action) || "commit".equals(action) || "rollback".equals(action))
        {
            return action;
        }
        if ("send events".equals(action) || "register memo state".equals(action))
        {
            return "report";
        }
        return null;
    }
}
