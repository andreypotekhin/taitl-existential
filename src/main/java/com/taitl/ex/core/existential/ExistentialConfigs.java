package com.taitl.ex.core.existential;

import java.io.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;

import static com.taitl.ex.common.helper.Args.*;

public class ExistentialConfigs implements Closeable
{
    protected Existential ex;
    protected ConfigurationLogic configLogic = new ConfigurationLogic(this);

    public ExistentialConfigs(Existential ex)
    {
        this.ex = ex;
    }

    public ConfigBuilder get(String op)
    {
        sane(op, "op");
        return configLogic.get(op);
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

    public Contexts contexts()
    {
        return configLogic.contexts();
    }
}
