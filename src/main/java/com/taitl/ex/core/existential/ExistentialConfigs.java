package com.taitl.ex.core.existential;

import java.io.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;

public class ExistentialConfigs implements Closeable
{
    protected Existential ex;
    protected ConfigurationLogic configLogic;

    public ExistentialConfigs(Existential ex)
    {
        this.ex = ex;
        this.configLogic = new ConfigurationLogic(this);
    }

    /**
     * Provides an instance of ConfigBuilder as starting point
     * for configuring a business operation. Repeat calls
     * result in the same instance returned as the initial call.
     * Called from Existential.configure(op)
     */
    public ConfigBuilder getBuilder(String op)
    {
        return configLogic.getBuilder(op);
    }

    /**
     * Called from ExistentialTransactions.begin() to indicate the all
     * setup/configuration activities, such as setting up validation
     * rules and event handlers, has been completed.
     * From this point, we stop accepting new contexts, custom transactions,
     * rules and handlers, to be able to freely cache the rules for best performance.
     */
    public void done()
    {
        configLogic.finalizeConfiguration();
    }

    /**
     * Called from ConfigBuilder.build() to indicate
     * that the configuration for an op has been completed.
     */
    public void onFinishConfiguration(String op)
    {
        configLogic.onFinishConfiguration(op);
    }

    public boolean isEmpty()
    {
        return configLogic.isEmpty();
    }

    public void close()
    {
        configLogic.close();
    }

    public Existential ex()
    {
        return ex;
    }

    public Config config(String op)
    {
        return configLogic.config(op);
    }
}
