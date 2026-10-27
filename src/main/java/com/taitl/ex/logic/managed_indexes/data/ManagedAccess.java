package com.taitl.ex.logic.managed_indexes.data;

import com.taitl.existential.exceptions.*;
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
            throw new ExistentialException("Managed index view is disposed. Begin a new transaction. "
                    + "See /Troubleshooting.md#managed-index-maintenance");
        }
        check.run();
    }

    public RuntimeException failure(String name, Throwable cause)
    {
        if (failed != null)
        {
            failed.accept(cause);
        }
        return new ExistentialException("Managed index '" + name + "' failed for operation '" + operation
                + "'. Check its subject types, non-null stable keys, and key extractor; rollback or close this handle. "
                + "See /Troubleshooting.md#managed-index-maintenance", cause);
    }

    public void close()
    {
        check = null;
        failed = null;
    }
}
