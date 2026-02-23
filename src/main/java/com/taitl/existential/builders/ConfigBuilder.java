package com.taitl.existential.builders;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;
import static com.taitl.ex.common.helper.Text.*;

/**
 * Builds a Config object for a single business operation as a set of Context
 * objects configured with constraints, invariants, intents, qualifiers and effects.
 */
public class ConfigBuilder
{
    /**
     * Name of business operation, e.g. "/app/docs/update",
     * or a wildcard name, "/app/docs/*"
     */
    String name;

    /**
     * Context(s) that apply to this operation. This includes main context
     * (e.g. "/app/docs/update") as well as any matching wildcard contexts
     * (e.g. "/app/docs/*")
     */
    List<Context> contexts = new ArrayList<>();

    /**
     * Factories for Context class. There is one default factory. Additional
     * factories are created by calling context() method.
     * The order of factories is important: execution order of rules follows
     * the order of factories, which follows the order of context() method calls.
     */
    protected Supplier<? extends Context> contextFactory = () -> Creator.create(Context.class);
    protected Supplier<? extends Transaction> transactionFactory = () -> Creator.create(Transaction.class);

    /**
     * Constructs Config object with specified name.
     * Examples: "/app/docs/update", "/app/docs/*"
     *
     * @param name Name of context
     */
    public ConfigBuilder(String name)
    {
        String trimmed = trimmed(name, "op");
        ContextKey.validate(trimmed);
        this.name = trimmed;
    }

    /**
     * Create ConfigBuilder for a context.
     *
     * @return ConfigBuilder for Context class
     */
    public ContextBuilder context(String name)
    {
        return new ContextBuilder(this, name);
    }

    /**
     * Associate a custom Context with the Op.
     *
     * Allows to define rules, such as invariants and intents,
     * for the context using an instance of a custom context class.
     *
     * Example:
     *   Ex.configure("/app/docs/update")                <-- ConfigBuilder
     *     .context(new Context(){{           <-- Custom context
     *        invariant(new Invariant<Document<JSON>>() {{
     *             write(doc -> doc.verify());
     *             all(doc -> doc.verified());
     *        }});
     *        invariant(new Invariant<Document<HTML>>() {{
     *             all(doc -> doc.fullyLoaded());
     *        }});
     *        allow(new Intent<Document<JSON>>() {{
     *             read();
     *             write();
     *        }});
     *        allow(new Intent<Document<HTML>>() {{
     *             read();
     *        }});
     *    }})
     *
     *  This method is a multi-entry method which allows creating multiple
     *  context factories when called sequentially. The reason to have
     *  multiple context factories is to be able to create multiple
     *  custom rules in different parts of application (e.g. in multiple
     *  classes/components).
     */
    public ConfigBuilder context(Context context)
    {
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
     * Build configured Contexts.
     * Create intermediates for consumption by subsequent stages.
     *
     * Do not call this method directly -tt is called by ConfigRegistry when
     * the first transaction begins.
     * This method is called from ExistentialConfigs.finalizeConfiguration().
     */
    public Config build(ExistentialConfigs ec)
    {
        // TODO:
        // Link each configured Context to direct or indirect parent Context
        // as well as to any matching wildcard Context(s) by comparing context names.
        // Do not create extra contexts
        // Do not copy or duplicate rules between context -
        // this may cause side effects to be called more than once

        Config config = Creator.create(Config.class);
        config.name(name);
        for (Context context : contexts)
        {
            config.addContext(context);
        }

        config.indexes().useFullClassNames(ec.ex().get(Flags.BEHAVIOR_TYPE_KEYS_USE_FULL_CLASS_NAMES));

        // Create intermediates for consumption by subsequent stages
        ec.onFinishConfiguration(name);
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
        transactionFactory = supplier;
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
    public Transaction createTransactionInstance()
    {
        return transactionFactory.get();
    }

    /**
     * Adds Context instance to Op.
     * Called by ConfigRegistry.create(op).
     *
     * @param cont Context to add
     */
    public void addContext(Context cont)
    {
        sane(cont, "cont");
        verify(!contexts.contains(cont), "This context is already added");
        contexts.add(cont);
    }
}
