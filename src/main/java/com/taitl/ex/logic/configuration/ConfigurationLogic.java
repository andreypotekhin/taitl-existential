package com.taitl.ex.logic.configuration;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.configuration.actions.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;

import java.io.*;
import java.util.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConfigurationLogic implements Closeable
{
    @Up
    protected ExistentialConfigs ec;

    @Logic
    protected ConfigRegistry registry;

    @Logic
    protected CreateBuilders createBuilders;

    @Logic
    protected BuildContexts buildContexts;

    @Logic
    protected FinalizeConfiguration finalizeConfiguration;

    protected ConfigBuilder configBuilder;
    public SetMap<String, Context> contexts = new SetMap<>();

    public ConfigurationLogic(ExistentialConfigs ec)
    {
        this.ec = ec;
        this.registry = Creator.create(ConfigRegistry.class, new Class[] { ConfigurationLogic.class }, this);
        this.createBuilders = Creator.create(CreateBuilders.class, new Class[] { ConfigurationLogic.class }, this);
        this.buildContexts = Creator.create(BuildContexts.class, new Class[] { ConfigurationLogic.class }, this);
        this.finalizeConfiguration =
                Creator.create(FinalizeConfiguration.class, new Class[] { ConfigurationLogic.class }, this);
    }

    /**
     * Creates (or returns already existing) instance of ConfigBuilder
     * as the starting point of configuring rules for this Existential instance.
     * Called by Existential.configure() method.
     */
    public ConfigBuilder getBuilder()
    {
        verify(!ec.ex().configured(),
                "Cannot call this method because setup has already been finalized");
        return createBuilders.getCreateBuilder();
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
     * Called from configuration finalization flow to indicate
     * that the configuration for an op has been completed.
     */
    public void onFinishConfiguration(String op)
    {
        finalizeConfiguration.onFinishConfiguration(op);
    }

    public void close()
    {
        configBuilder = null;
        contexts.clear();
    }

    /**
     * Add all configured rules to the indexes.
     */
    public void indexConfig(String op)
    {
        sane(op, "op");
        Config config = registry.get(op);
        synchronized (config)
        {
            ConfigurationIndexes validationIndexes = config.indexes(op, StageName.VALIDATION);
            if (validationIndexes.configuredHandlers.ready() && validationIndexes.configuredIntents.ready())
            {
                return;
            }
            for (StageName stageName : StageName.values())
            {
                config.indexes(op, stageName).indexConfig(op, config, stageName);
            }
        }
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

    public ConfigBuilder configBuilder()
    {
        return configBuilder;
    }

    public void configBuilder(ConfigBuilder configBuilder)
    {
        this.configBuilder = configBuilder;
    }

    public Config config(String op)
    {
        sane(op, "op");
        Config config = registry.get(op);
        indexConfig(op);
        return config;
    }
}
