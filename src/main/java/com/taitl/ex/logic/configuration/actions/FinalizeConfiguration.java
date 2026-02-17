package com.taitl.ex.logic.configuration.actions;

import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.*;

import static com.taitl.ex.common.helper.Args.*;

public class FinalizeConfiguration
{
    protected ConfigurationLogic cl;
    protected ExistentialConfigs ec;
    protected Existential ex;

    public FinalizeConfiguration(ConfigurationLogic cl)
    {
        this.cl = cl;
        this.ec = cl.ec();
        this.ex = cl.ex();
    }

    /**
     * Called from ExistentialTransactions.begin() to indicate the all
     * setup/configuration activities, such as setting up validation
     * rules and event handlers, has been completed.
     *
     * From this point, we stop accepting new contexts, custom transactions,
     * rules and handlers, to be able to freely cache for best performance.
     */
    public void finalizeConfiguration()
    {
        if (!ex.configured())
        {
            if (cl.isEmpty())
            {
                throw new IllegalStateException("You need to configure at least one context");
            }

            synchronized (Existential.class)
            {
                ex.configured(true);

                // Now that we finalized setting up rules and event handlers
                // create all (parent, intermediate) contexts for all the Contexts
                // configured so far. This will result in a call to each
                // and every intent(), effect() method of each custom context,
                // and therefore will create instances of each Invariant, Intent
                // provided during setup.
                cl.buildConfigs();
            }
        }
    }

    /**
     * Called from ConfigBuilder.build() to indicate
     * that the configuration for an op has been completed.
     */
    public void onFinishConfiguration(String op)
    {
        sane(op, "op");

        // TODO: create intermediaries for next stages
    }
}
