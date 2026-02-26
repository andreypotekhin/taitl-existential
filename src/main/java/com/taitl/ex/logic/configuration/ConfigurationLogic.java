package com.taitl.ex.logic.configuration;

import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.configuration.actions.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;

import java.io.*;
import java.util.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConfigurationLogic implements Closeable
{
    protected Map<String, ConfigBuilder> configBuilders = new LinkedHashMap<>();
    public SetMap<String, Context> contexts = new SetMap<>();

    protected ExistentialConfigs ec;

    protected ConfigRegistry registry;
    protected CreateBuilders createBuilders;
    protected BuildContexts buildContexts;
    protected FinalizeConfiguration finalizeConfiguration;

    public ConfigurationLogic(ExistentialConfigs ec)
    {
        this.ec = ec;
        this.registry = new ConfigRegistry(this);
        this.createBuilders = new CreateBuilders(this);
        this.buildContexts = new BuildContexts(this);
        this.finalizeConfiguration = new FinalizeConfiguration(this);
    }

    /**
     * Creates (or returns already existing) instance of ConfigBuilder
     * as the starting point of configuring rules for a business operation.
     * Called by Existential.config() method.
     */
    public ConfigBuilder getBuilder(String op)
    {
        sane(op, "op");
        verify(!ec.ex().configured(),
                "Cannot call this method because setup has already been finalized");
        return createBuilders.getCreateBuilder(op);
    }

    /**
     * Get (create if missing) the contexts for business operation.
     * Operation name is a non-wildcarded, for instance, "/app/flights/update"
     * When parent or wildcard contexts are defined, multiple contexts may match
     * a single operation: "/app/flights/update", "/app/flights", "/app/*"
     * Create a new Context object if no matching context already exist.
     * Create all parent Context object if this context is not a root context (/).
     *
     * Example: call("/app/flights/update") will create these three contexts,
     * tied by parent-child relationship:
     * "/app/flights/update"
     * "/app/flights"
     * "/app"
     * "/"
     * of which it will return the top one ("/app/flights/update")
     */
    public List<Context> buildContexts(String op)
    {
        return buildContexts.call(op);
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
        finalizeConfiguration.call();
    }

    /**
     * Called from ConfigBuilder.build() to indicate
     * that the configuration for an op has been completed.
     */
    public void onFinishConfiguration(String op)
    {
        finalizeConfiguration.onFinishConfiguration(op);
    }

    public void close()
    {
        configBuilders.clear();
        contexts.clear();
    }

    /**
     * Add all configured rules to the indexes.
     */
    public void indexConfig(String op)
    {
        sane(op, "op");
        Config config = registry.get(op);
        config.indexes().indexConfig(op, config);
    }

    /* Attributes */

    public boolean isEmpty()
    {
        return registry.isEmpty();
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
        verify(config != null, String.format("Config not find for op '%s'", op));
        return config;
    }
}
