package com.taitl.existential.builders;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.configuration.rules.*;
import com.taitl.existential.Existential;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;
import static com.taitl.ex.common.helper.strings.Text.*;

/**
 * Builds full configuration for a single Existential instance based on declared contexts.
 *
 * @see ContextBuilder
 */
public class ConfigBuilder
{
    protected boolean requireBehaviorDescriptions;

    /**
     * Contexts declared for this Existential instance.
     */
    List<Context> contexts = new ArrayList<>();
    List<ContextBuilder> contextBuilders = new ArrayList<>();

    /**
     * Default context and transaction factories used by new context builders.
     */
    protected Supplier<? extends Context> contextFactory = () -> Creator.create(Context.class);
    protected BiFunction<String, String, ? extends Transaction> transactionFactory = Transaction.FACTORY;

    public ConfigBuilder(Existential ex)
    {
        sane(ex, "ex");
        this.requireBehaviorDescriptions = ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
    }

    /**
     * Builds a custom Context for the specified business operation.
     * Allows defining rules, such as invariants and intents,
     * for the context using a ContextBuilder.
     *
     * Example:
     *   Ex.configure()                 <-- ConfigBuilder (this)
     *     .context("/app/docs/update") <-- ContextBuilder
     *        .intent(new TypeKey<Intent<Document<?>>>(){})
     *             .read()
     *             .write()
     *        .effect(new TypeKey<Effect<Document<HTML>>>(){})
     *             .write(doc -> doc.spellCheck())
     *        .effect(new TypeKey<Document<JSON>>(){})
     *             .write(doc -> doc.validate())
     *        .invariant(Document.class)
     *             .all(doc -> doc.valid());
     *
     *  This method allows creating multiple calls with same operation name,
     *  to be able to append custom rules from different parts of the application
     *  (for example, in multiple classes or components).
     *
     * @return ContextBuilder for Context class
     */
    public ContextBuilder context(String name)
    {
        ContextBuilder contextBuilder = Creator.create(ContextBuilder.class,
                new Class<?>[] { ConfigBuilder.class, String.class },
                this,
                contextName(name));
        contextBuilders.add(contextBuilder);
        return contextBuilder;
    }

    /**
     * Associates a custom Context instance with the Op.
     *
     * Allows defining rules, such as invariants and intents,
     * for the context using an instance of a custom context class.
     *
     * Example:
     *   Ex.configure()                              <-- ConfigBuilder
     *     .context(new Context("/app/docs/update") <-- Custom context
     *        .intent(new TypeKey<Intent<Document<?>>>(){})
     *             .read()
     *             .write()
     *        .effect(new TypeKey<Effect<Document<HTML>>>(){})
     *             .write(doc -> doc.spellCheck())
     *        .effect(new TypeKey<Effect<Document<JSON>>>(){})
     *             .write(doc -> doc.validate())
     *        .invariant(new TypeKey<Invariant<Document>>(){})
     *             .all(doc -> doc.valid());
     *
     *  This method is a multi-entry method that allows creating multiple
     *  context factories when called sequentially. The reason to have
     *  multiple context factories is to create multiple custom rules in
     *  different parts of the application (for example, in multiple classes
     *  or components).
     */
    public ConfigBuilder context(Context context)
    {
        sane(context, "context");
        context.op(contextName(context.name()));

        // Guard against multiple calls to .context() with same argument,
        // for instance, if such call exists somewhere in the middle of
        // ordinary request processing (e.g. in a controller)
        if (!contexts.contains(context))
        {
            // TODO: test for different contexts to not replace each other
            addContext(context);
        }
        return this;
    }

    /**
     * Builds configured Contexts.
     * Creates intermediates for consumption by subsequent stages.
     *
     * Do not call this method directly. It is called from the configuration
     * finalization flow when the first transaction begins.
     * This method is called from ExistentialConfigs.finalizeConfiguration().
     */
    public Config build(ExistentialConfigs ec)
    {
        sane(ec, "ec");
        buildContexts();
        // TODO:
        // Link each configured Context to direct or indirect parent Context
        // as well as to any matching wildcard Context(s) by comparing context names.
        // Do not create extra contexts
        // Do not copy or duplicate rules between contexts -
        // this may cause side effects to be called more than once

        Config config = Creator.create(Config.class);
        boolean useFullClassNames = ec.ex().get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
        for (Context context : contexts)
        {
            config.addContext(context);
        }
        applyTypeKeyNaming(config, useFullClassNames);
        return config;
    }

    /**
     * Specify a context factory for this operation.
     *
     * @param supplier Custom context factory
     * @return This
     */
    public ConfigBuilder contextFactory(Supplier<? extends Context> supplier)
    {
        sane(supplier, "supplier");
        contextFactory = supplier;
        return this;
    }

    /**
     * Specify a transaction factory for this operation.
     *
     * @param supplier Custom transaction factory
     * @return This
     *
     */
    public ConfigBuilder transactionFactory(Supplier<? extends Transaction> supplier)
    {
        sane(supplier, "supplier");
        transactionFactory = (op, name) -> supplier.get();
        return this;
    }

    /**
     * Specify a transaction factory for this operation.
     *
     * @param factory Custom transaction factory that receives operation and transaction name
     * @return This
     */
    public ConfigBuilder transactionFactory(BiFunction<String, String, ? extends Transaction> factory)
    {
        sane(factory, "factory");
        transactionFactory = factory;
        return this;
    }

    /**
     * Creates a new Context instance for this operation.
     *
     * @return Context instance
     */
    public Context createContextInstance()
    {
        return contextFactory.get();
    }

    /**
     * Creates a new Transaction instance for this operation.
     *
     * @return Transaction instance
     */
    public Transaction createTransactionInstance(String op, String name)
    {
        sane(op, "op", name, "name");
        return transactionFactory.apply(op, name);
    }

    public boolean requireBehaviorDescriptions()
    {
        return requireBehaviorDescriptions;
    }

    /**
     * Adds Context instance to this builder.
     *
     * @param cont Context to add
     */
    public void addContext(Context cont)
    {
        sane(cont, "cont");
        verify(!contexts.contains(cont), "This context is already added");
        contexts.add(cont);
    }

    void buildContexts()
    {
        for (ContextBuilder contextBuilder : contextBuilders)
        {
            contextBuilder.buildContext();
        }
    }

    protected String requireContextNameMatchesParentContext(String contextName, String parentContextName)
    {
        return MatchParentName.require(contextName, parentContextName, "parent context");
    }

    protected void applyTypeKeyNaming(Config config, boolean useFullClassNames)
    {
        sane(config, "config");
        config.useFullClassNames(useFullClassNames);
    }

    protected String contextName(String name)
    {
        String trimmedName = trimmed(name, "name");
        ContextKey.validate(trimmedName);
        return trimmedName;
    }
}
