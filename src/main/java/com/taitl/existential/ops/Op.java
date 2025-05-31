package com.taitl.existential.ops;

import java.util.*;
import java.util.function.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import com.taitl.exlogic.unused.builders.*;

/**
 * Defines a single business Operation as a set of Context objects 
 * configured with constraints, invariants, intents, qualifiers and effects.
 *
 * Multiple Contexts which may apply to same a business operation: the main
 * context, all its parent contexts, as well as any matching wildcard contexts.
 * They are stored separately from each other in the OpRegistry.
 *
 * The contexts are stored in the order of being declared. Within each context,
 * the transactions are stored in the order transaction factories are declared.
 *
 * @author Andrey Potekhin
 */
public class Op
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
     * Constructs Op object with specified name.
     * Examples: "/app/docs/update", "/app/docs/*"
     *
     * @param name Name of context
     */
    public Op(String name)
    {
        Args.cool(name, "op");
        ContextKey.validate(name);
        this.name = name.trim();
    }

    /**
     * Associate a custom Context class with Op.
     *
     * Associating a custom Context with Op allows to define
     * rules, such as invariants and intents, for the context using
     * an instance of a custom context class.
     *
     * Example:
     *   Contexts.configure("/app/docs/update")                <-- Op
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
    public Op context(Context context)
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

    /**
     * Create ConfigBuilder for a context.
     *
     * @return ConfigBuilder for Context class
     */
    public ConfigBuilder context()
    {
        return new ConfigBuilder(this, ConfigBuilder.TargetType.TYPE_CONTEXT);
    }

    /**
     * Create ConfigBuilder for a transaction.
     *
     * @return ConfigBuilder for transaction
     */
    public ConfigBuilder transaction()
    {
        return new ConfigBuilder(this, ConfigBuilder.TargetType.TYPE_TRANSACTION);
    }

    /**
     * Adds Context instance to Op.
     * Called by OpRegistry.create(op).
     *
     * @param cont Context to add
     */
    public void addContext(Context cont)
    {
        Args.cool(cont, "cont");
        State.verify(!contexts.contains(cont), "This context is already added");
        ContextKey key = ContextKey.from(cont.name());
        State.verify(!name.equals(key.toString()), "Can't add context to itself");
        contexts.add(cont);
        // instructions.add(new ContextRef(cont));
    }

    /**
     * For each Context within Op, create instances of custom
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
    }

    /**
     * Specify a context factory for this operation.
     *
     * @param supplier Custom context factory
     * @return This
     */
    public Op factory(Supplier<? extends Context> supplier)
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
    public Op transactionFactory(Supplier<? extends Transaction> supplier)
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
