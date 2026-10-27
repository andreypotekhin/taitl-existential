package com.taitl.ex.logic.diagnostics.actions;

import com.taitl.ex.logic.diagnostics.data.*;
import com.taitl.existential.diagnostics.*;
import java.util.*;

/** Detached semantic details; no original exception or application payload becomes snapshot state. */
public class BuildOperationalDiagnostic
{
    public Diagnostic call(DiagnosticCode code, String detail, String operation)
    {
        String remedy;
        switch (code)
        {
        case RULE_REJECTED:
        case CALLBACK_FAILED:
            remedy = null;
            break;
        case CONFIGURATION_INVALID:
            remedy = "Correct the configuration source or declaration before retrying begin";
            break;
        case CONFIGURATION_FROZEN:
            remedy = "Configure a new library instance before beginning its first transaction";
            break;
        case TRANSACTION_CLOSED:
            remedy = "Begin a new transaction for additional work";
            break;
        case LIBRARY_CLOSED:
            remedy = "Finish existing cleanup and use a new explicitly configured library instance";
            break;
        case SHUTDOWN_TIMEOUT:
        case SHUTDOWN_INTERRUPTED:
            remedy = "Finish or close active handles, then retry library close; shutdown does not run rollback callbacks";
            break;
        case REENTRANT_LIFECYCLE:
            remedy = "Return from the callback before closing its transaction or library";
            break;
        case MEMO_INVALID:
            remedy = "Register one detached before-state per live entity and type before mutation";
            break;
        case MEMO_MISSING:
            remedy = "Register the before-state with memo() before mutation, or report explicit before/after values";
            break;
        case INDEX_MAINTENANCE_FAILED:
            remedy = "Correct the extractor or key, explicitly roll back or close the failed handle, then retry fresh";
            break;
        case CLEANUP_FAILED:
            remedy = "Inspect the original cause and suppressed failures; release application resources explicitly";
            break;
        default:
            throw new IllegalArgumentException("Unsupported operational diagnostic: " + code);
        }
        String message = code.id().toLowerCase(Locale.ROOT).replace('-', ' ');
        return new DiagnosticSnapshot(code, message, operation, null, null, false, null, null, null, null,
                null, null, false, detail == null ? List.of() : List.of(detail),
                remedy == null ? List.of() : List.of(remedy),
                "/Troubleshooting.md#diagnostic-" + code.id().toLowerCase(Locale.ROOT), List.of());
    }
}
