package com.taitl.ex.logic.configuration.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;

import static com.taitl.ex.common.helper.State.*;

public class FinalizeConfiguration
{
    @Up
    protected ConfigurationLogic cl;

    @Up
    protected ExistentialConfigs ec;

    @Up
    protected Existential ex;

    @Logic
    protected BuildConfig buildConfig;

    public FinalizeConfiguration(ConfigurationLogic cl)
    {
        this.cl = cl;
        this.ec = cl.ec();
        this.ex = cl.ex();
        this.buildConfig = Creator.create(BuildConfig.class, new Class[] { ConfigurationLogic.class }, cl);
    }

    /**
     * Called from ConfigurationLogic.finalizeConfiguration() to indicate the all
     * setup/configuration activities, such as declaring validation
     * rules and event handlers, has been completed.
     * From this point, we stop accepting new contexts, custom transactions,
     * rules and handlers, to be able to freely cache for best performance.
     */
    public void call()
    {
        if (!ex.configured())
        {
            if (cl.isEmpty() && cl.configBuilder() == null)
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
                buildConfig();
            }
        }
    }

    public void buildConfig()
    {
        ConfigBuilder configBuilder = cl.configBuilder();
        verify(configBuilder != null, "No config builder exists");
        buildConfig.call(configBuilder);
        // Configuration done. Prevent any further use of configBuilder.
        cl.configBuilder(null);
    }

    /**
     * Called from configuration finalization flow to indicate
     * that the configuration for an op has been completed
     * and to create intermediaries for the other processing stages.
     */
    public void onFinishConfiguration(String op)
    {
        cl.indexConfig(op);
    }
}
