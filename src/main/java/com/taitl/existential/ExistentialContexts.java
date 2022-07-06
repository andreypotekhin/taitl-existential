package com.taitl.existential;

import com.taitl.existential.contexts.OpContextRegistry;
import com.taitl.existential.contexts.OpContext;
import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;

public class ExistentialContexts
{
    protected Existential ex;
    protected OpContextRegistry registry = new OpContextRegistry();

    public ExistentialContexts(Existential ex)
    {
        this.ex = ex;
    }

    public OpContext configure(String op)
    {
        Args.cool(op, "op");
        State.verify(!ex.finalized,
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
    public void finalizeSetup()
    {
        if (!ex.finalized)
        {
            if (isEmpty())
            {
                throw new IllegalStateException("You need to configure at least one context");
            }

            synchronized (Existential.class)
            {
                ex.finalized = true;

                // Now that we finalized set up of rules and event handlers
                // we'll create custom contexts for all OpContexts that
                // exist context registry. This will result in a call to each
                // and every require(), intent() method of each custom context,
                // and therefore create instances of each Invariant, Intent
                // provided during the setup.
                registry.createContexts();
            }
        }
    }

    public boolean isEmpty()
    {
        return registry.isEmpty();
    }
}
