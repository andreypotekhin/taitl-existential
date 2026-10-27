package com.taitl.ex.logic.managed_indexes.data;

import com.taitl.existential.exceptions.*;
import com.taitl.existential.diagnostics.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.diagnostics.*;
import java.util.function.*;

/** Guards escaped views without retaining transaction callbacks after disposal. */
public class ManagedAccess
{
    private Runnable check;
    private Consumer<Throwable> failed;
    private final String operation;

    public ManagedAccess(String operation, Runnable check, Consumer<Throwable> failed)
    {
        this.operation = operation;
        this.check = check;
        this.failed = failed;
    }

    public void check()
    {
        if (check == null)
        {
            throw Creator.singleton(TransactionDiagnosticLogic.class).disposedView(operation);
        }
        check.run();
    }

    public RuntimeException failure(String name, Throwable cause)
    {
        if (failed != null)
        {
            failed.accept(cause);
        }
        return OperationalDiagnostics.failure(DiagnosticCode.INDEX_MAINTENANCE_FAILED, "Managed index '" + name
                + "' failed for operation '" + operation
                + "'. Check its subject types, non-null stable keys, and key extractor; rollback or close this handle. "
                + "See /Troubleshooting.md#managed-index-maintenance", operation, cause);
    }

    public void close()
    {
        check = null;
        failed = null;
    }
}
