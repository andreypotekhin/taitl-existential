package com.taitl.ex.logic.configuration;

import java.io.*;
import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.configuration.actions.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.contexts.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConfigurationLogic implements Closeable
{
    protected Map<String, ConfigBuilder> configBuilders = new LinkedHashMap<>();
    public Multimap<String, Context> contexts = new Multimap<>();

    protected ExistentialConfigs ec;

    protected CreateConfigBuilders builders = new CreateConfigBuilders(this);
    protected ConfigRegistry registry = new ConfigRegistry(this);
    protected BuildContexts buildContexts = new BuildContexts(this);
    protected BuildConfigs buildConfigs = new BuildConfigs(this);
    protected FinalizeConfiguration finalizeConfiguration = new FinalizeConfiguration(this);

    public ConfigurationLogic(ExistentialConfigs ec)
    {
        this.ec = ec;
    }

    public ConfigBuilder getBuilder(String op)
    {
        sane(op, "op");
        verify(!ec.ex().configured(),
                "Cannot call this method because setup has already been finalized");
        return builders.getcreateBuilder(op);
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
        finalizeConfiguration.finalizeConfiguration();
    }

    /**
     * Called from ConfigBuilder.build() to indicate
     * that the configuration for an op has been completed.
     */
    public void onFinishConfiguration(String op)
    {
        finalizeConfiguration.onFinishConfiguration(op);
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
        return ec.ex();
    }

    public ExistentialConfigs ec()
    {
        return ec;
    }

    public ConfigRegistry registry()
    {
        return registry;
    }

    public Map<String, ConfigBuilder> configBuilders()
    {
        return configBuilders;
    }

    public Config config(String op)
    {
        sane(op, "op");
        Config config = registry.configs().get(op);
        verify(config != null, String.format("Config not found for op '%s'", op));
        return config;
    }

    public List<Context> buildContexts(String op)
    {
        return buildContexts.buildContexts(op);
    }

    public void buildConfigs()
    {
        verify(!configBuilders.isEmpty(), "No config builders exist");
        buildConfigs.buildConfigs(configBuilders);
    }
}
