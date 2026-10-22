package com.taitl.ex.logic.library;

import com.taitl.ex.core.existential.*;
import com.taitl.existential.*;

import java.io.*;
import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/** Coordinates transaction draining and library component cleanup. */
public class ShutdownLogic
{
    protected final Existential ex;

    protected final List<Closeable> components;
    protected boolean closed;

    public ShutdownLogic(Existential ex, ExistentialEvents events, ExistentialFlags flags,
            ExistentialConfigs configs, ExistentialAccess access, ExistentialInit init)
    {
        sane(ex, "ex", events, "events", flags, "flags", configs, "configs", access, "access", init, "init");
        this.ex = ex;
        components = Arrays.asList(events, flags, configs, access, init);
    }

    public void close(ShutdownPolicy policy)
    {
        sane(policy, "policy");
        ex.transactions().logic().ensureShutdownAllowed();
        synchronized (this)
        {
            if (closed)
            {
                return;
            }

            ex.transactions().logic().close(policy);
            Throwable failure = null;
            for (Closeable component : components)
            {
                try
                {
                    component.close();
                }
                catch (Throwable caught)
                {
                    if (failure == null)
                    {
                        failure = caught;
                    }
                    else if (failure != caught)
                    {
                        failure.addSuppressed(caught);
                    }
                }
            }
            if (failure != null)
            {
                rethrow(failure);
            }
            closed = true;
        }
    }

    protected void rethrow(Throwable failure)
    {
        if (failure instanceof RuntimeException)
        {
            throw (RuntimeException) failure;
        }
        if (failure instanceof Error)
        {
            throw (Error) failure;
        }
        throw new IllegalStateException("Library cleanup failed", failure);
    }
}
