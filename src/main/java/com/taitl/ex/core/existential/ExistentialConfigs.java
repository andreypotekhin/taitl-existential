package com.taitl.ex.core.existential;

import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;

import java.io.*;

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
     * Provides a {@link ConfigBuilder} as starting point for configuring rules
     * for this Existential instance. Repeat calls return the same builder.
     * Called from Existential.configure().
     */
    public ConfigBuilder getBuilder()
    {
        return configLogic.getBuilder();
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
