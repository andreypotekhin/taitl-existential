package com.taitl.ex.logic.configuration;

import java.io.*;
import com.taitl.ex.core.existential.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConfigurationLogic implements Closeable
{
    protected Existential ex;
    protected ExistentialConfigs ec;

    protected ConfigRegistry registry = new ConfigRegistry(this);
    protected Contexts contexts = new Contexts();

    public ConfigurationLogic(ExistentialConfigs ec)
    {
        this.ec = ec;
        this.ex = ec.ex();
    }

    public ConfigBuilder get(String op)
    {
        sane(op, "op");
        verify(!ex.configured(),
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
    public void finalizeConfiguration()
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

                // Now that we finalized setting up rules and event handlers
                // create all (parent, intermediate) contexts for all the Contexts
                // configured so far. This will result in a call to each
                // and every intent(), effect() method of each custom context,
                // and therefore will create instances of each Invariant, Intent
                // provided during setup.
                registry.finalizeConfiguration();
            }
        }
    }

    /**
     * Called from ConfigBuilder.build() to indicate
     * that the configuration for an op has been completed.
     */
    public void onFinishConfiguration(String op)
    {
        // TODO: create intermediaries for next stages
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

    public ExistentialConfigs ec()
    {
        return ec;
    }

    public Contexts contexts()
    {
        return contexts;
    }
}
