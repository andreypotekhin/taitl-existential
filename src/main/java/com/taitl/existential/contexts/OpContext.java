package com.taitl.existential.contexts;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.taitl.existential.config.builders.ConfigBuilder;
import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;
import com.taitl.existential.keys.ContextKey;
import com.taitl.existential.transactions.Transaction;

/**
 * Defines an Operational Context - a set of Context objects relevant
 * to a single business operation.
 *
 * There may be multiple OpContexts which may apply to a business operation: main
 * context as well as wildcard contexts. They are stored separately from each
 * other in OpContextRegistry.
 *
 * For each OpContext, the user may also create several custom Context objects,
 * by repeatedly calling .context() method to specify a context factory and
 * the rules (invariants and intents) that apply. So within one OpContext
 * there are several smaller Contexts.
 *
 * This class's job is to hold instances of all these contexts.
 *
 * The Context may declare entity event handlers, which result in side effects,
 * so the order of the contexts is important.
 *
 * The order of contexts follows the order of declaration of their corresponding
 * contexts (contexts which are applicable to business operation). Within each context,
 * the order of transactions is same as the order of declaration of its transaction
 * factories.
 */
public class OpContext
{
    protected static final Supplier<? extends Context> DEFAULT_CONTEXT_FACTORY =
            () -> new Context("undefined");

    protected static final Supplier<? extends Transaction> DEFAULT_TRANSACTION_FACTORY =
            () -> new Transaction("undefined");

    /**
     * Name of context matching business operation, e.g. "/app/docs/update",
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
     * Main context is the one with name matching op name, e.g. "/apps/docs/update"
     * All other contexts are wildcard contexts.
     */
    // Context mainContext;

    /**
     * Instructions - event handlers. Includes all event handlers (rules)
     * defined in all contexts.
     * TODO: add ContextRef instruction to each context
     */
    // protected Instructions instructions = new Instructions();

    /**
     * Expressions, such as All<T>, defined in all contexts.
     * TODO: add ContextRef instruction to each context
     */
    // protected Expressions expressions = new Expressions();

    /**
     * Factories for Context class. There is one default factory. Additional
     * factories are created by calling context() method.
     * The order of factories is important: execution order of rules follows
     * the order of factories, which follows the order of context() method calls.
     */
    // Set<Supplier<? extends Context>> contextFactories = new LinkedHashSet<>(1);
    // {
    // contextFactories.add(DEFAULT_CONTEXT_FACTORY);
    // }
    protected Supplier<? extends Context> contextFactory = DEFAULT_CONTEXT_FACTORY;
    protected Supplier<? extends Transaction> transactionFactory = DEFAULT_TRANSACTION_FACTORY;

    /**
     * Constructs OpContext object with specified name.
     * Examples: "/app/docs/update", "/app/docs/*"
     *
     * @param name Name of context
     */
    public OpContext(String name)
    {
        Args.cool(name, "op");
        ContextKey.requireValidName(name);
        this.name = name.trim();
    }

    /**
     * Associate a custom Context class with OpContext.
     *
     * Associating a custom Context with OpContext allows to define
     * rules, such as invariants and intents, for the context using
     * an instance of a custom context class.
     *
     * Example:
     *   Contexts.configure("/app/docs/update")                <-- OpContext
     *     .context(new Context(){{           <-- Custom context
     *        ensure(new Invariant<Document<JSON>>() {{
     *             write(doc -> doc.verify());
     *             all(doc -> doc.verified());
     *        }});
     *        ensure(new Invariant<Document<HTML>>() {{
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
    public OpContext context(Context context)
    {
        // Guard against multiple calls to .context() with same argument,
        // for instance, if such call exists somewhere in the middle of
        // ordinary request processing (e.g. in a controller)
        if (!contexts.contains(contexts))
        {
            // TODO: test for different contexts to not replace each other
            contexts.add(context);
        }
        return this;
    }

    // public OpContext context(Supplier<? extends Context> supplier)
    // {
    // if (contextFactories.size() == 1)
    // {
    // if (contextFactories.contains(DEFAULT_CONTEXT_FACTORY))
    // {
    // // Replace default context factory
    // contextFactories.remove(DEFAULT_CONTEXT_FACTORY);
    // }
    // }
    // // Guard against multiple calls to .context() with same arguments,
    // // for instance, if such call exists somewhere in the middle of
    // // ordinary request processing (e.g. in a controller)
    // if (!contextFactories.contains(supplier))
    // {
    // // TODO: test for different suppliers to not replace each other
    // contextFactories.add(supplier);
    // }
    // return this;
    // }

    /**
     * Create ConfigBuilder for Context class.
     *
     * @return ConfigBuilder for Context class
     */
    public ConfigBuilder context()
    {
        return new ConfigBuilder(this, ConfigBuilder.TargetType.TYPE_CONTEXT);
    }

    /**
     * Create ConfigBuilder for transaction.
     *
     * @return ConfigBuilder for transaction
     */
    public ConfigBuilder transaction()
    {
        return new ConfigBuilder(this, ConfigBuilder.TargetType.TYPE_TRANSACTION);
    }

    /**
     * Adds Context instance to OpContext.
     * Called by OpContextRegistry.create(op).
     *
     * @param cont Context to add
     */
    public void addContext(Context cont)
    {
        Args.cool(cont, "cont");
        State.verify(!contexts.contains(cont), "This context is already added");
        ContextKey key = new ContextKey(cont.name);
        State.verify(name.equals(key.name), "This context is already added");
        contexts.add(cont);
        // instructions.add(new ContextRef(cont));
    }

    /**
     * For each Context within OpContext, create instances of custom
     * Contexts using provided context factories, and add them separately
     * to each Context.add().
     *
     * Problem: this duplicates the rules, and therefore, side effects,
     * which is not desirable!
     *
     * This method is called from ExistentialContexts.finalizeSetup(),
     * one time, when the first transaction begins.
     */
    public void createSubcontexts()
    {
        // Already created

        // State.verify(!contextFactories.isEmpty(), "contextFactories must not be empty");
        // State.verify(!contexts.isEmpty(), "Field 'contexts' must not be empty");
        // for (Context context : contexts)
        // {
        // for (Supplier<? extends Context> supplier : contextFactories)
        // {
        // Context custom = supplier.get();
        // context.add(custom);
        // }
        // }
    }

    /*
     * public List<Context> createContexts() { State.verify(!contextFactories.isEmpty(),
     * "contextFactories must not be empty"); List<Context> result = new
     * ArrayList<>(contextFactories.size()); for (Supplier<? extends Context> supplier :
     * contextFactories) { Context cont = supplier.get(); result.add(cont); } return result; }
     */

    /**
     * Specify a context factory for this operation.
     *
     * @param supplier Custom context factory
     * @return This
     */
    public OpContext factory(Supplier<? extends Context> supplier)
    {
        Args.cool(supplier, "supplier");
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
    public OpContext transactionFactory(Supplier<? extends Transaction> supplier)
    {
        Args.cool(supplier, "supplier");
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
}
