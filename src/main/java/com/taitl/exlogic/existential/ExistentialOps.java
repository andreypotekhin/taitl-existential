package com.taitl.exlogic.existential;

import java.io.*;
import com.taitl.existential.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.ops.*;
import com.taitl.existential.helper.*;
import com.taitl.exlogic.contexts.*;

public class ExistentialOps implements Closeable
{
    protected Existential ex;
    protected OpRegistry registry = new OpRegistry(this);
    protected Contexts contexts = new Contexts();

    public ExistentialOps(Existential ex)
    {
        this.ex = ex;
    }

    public Op configure(String op)
    {
        Args.cool(op, "op");
        State.verify(!ex.configured(),
                "Cannot call this method because setup has already been finalized");
        return registry.getcreate(op);
    }

    /**
     * Called from ExistentialTransactions.begin() to indicate the all
     * setup/configuration activities, such as setting up validation
     * rules and event handlers, has been completed.
     *
     * From this point, we stop accepting new contexts, custom transactions,
     * rules and handlers, to be able to freely cache for best performance.
     */
    public void finalise()
    {
        if (!ex.configured())
        {
            if (isEmpty())
            {
                throw new IllegalStateException("You need to configure at least one context");
            }

            synchronized (Existential.class)
            {
                ex.configured(true);

                // Now that we finalized set up of rules and event handlers
                // we'll create custom contexts for all OpContexts that
                // exist context registry. This will result in a call to each
                // and every require(), intent() method of each custom context,
                // and therefore create instances of each Invariant, Intent
                // provided during the setup.
                registry.createSubcontexts();
            }
        }
    }

    public boolean isEmpty()
    {
        return registry.isEmpty();
    }

    public void close()
    {
    }

    public Existential ex()
    {
        return ex;
    }

    public Contexts contexts()
    {
        return contexts;
    }
}
