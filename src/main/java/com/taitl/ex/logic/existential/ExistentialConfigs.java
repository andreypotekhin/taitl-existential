package com.taitl.ex.logic.existential;

import java.io.*;
import com.taitl.ex.domain.configuration.*;
import com.taitl.ex.domain.contexts.*;
import com.taitl.existential.*;
import com.taitl.existential.configuration.builders.*;
import com.taitl.existential.helper.*;

public class ExistentialConfigs implements Closeable
{
    protected Existential ex;
    protected ConfigRegistry registry = new ConfigRegistry(this);
    protected Contexts contexts = new Contexts();

    public ExistentialConfigs(Existential ex)
    {
        this.ex = ex;
    }

    public ConfigBuilder get(String op)
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
                // we'll create custom contexts for all Contexts that
                // exist in context registry. This will result in a call to each
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
