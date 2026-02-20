package com.taitl.ex.logic.configuration.actions;

import java.util.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.State.*;

public class FinalizeConfiguration
{
    protected ConfigurationLogic cl;
    protected ExistentialConfigs ec;
    protected Existential ex;

    protected BuildConfigs buildConfigs;

    public FinalizeConfiguration(ConfigurationLogic cl)
    {
        this.cl = cl;
        this.ec = cl.ec();
        this.ex = cl.ex();
        this.buildConfigs = new BuildConfigs(cl);
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
                buildConfigs();
            }
        }
    }

    public void buildConfigs()
    {
        Map<String, ConfigBuilder> configBuilders = cl.configBuilders();
        verify(!configBuilders.isEmpty(), "No config builders exist");
        buildConfigs.call(configBuilders);
        // Configuration done. Prevent any further use of configBuilders.
        configBuilders.clear();
    }

    /**
     * Called from ConfigBuilder.build() to indicate
     * that the configuration for an op has been completed
     * and to create intermediaries for the other processing stages.
     */
    public void onFinishConfiguration(String op)
    {
        Config config = cl.registry().get(op);
        config.indexes().indexConfig(op, config);
    }
}
