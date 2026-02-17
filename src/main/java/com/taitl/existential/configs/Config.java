package com.taitl.existential.configs;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Defines a single business Operation as a set of Context objects
 * configured with constraints, invariants, intents, qualifiers and effects.
 *
 * Multiple Contexts which may apply to same business operation: the main
 * context, all its parent contexts, as well as any matching wildcard contexts.
 *
 * The contexts are stored in the order of being declared.
 *
 *  @see Context
 */
public class Config
{
    /**
     * Name of business operation, e.g. "/app/docs/update",
     * or a wildcard name, "/app/docs/*"
     */
    protected String name;

    /**
     * Context(s) that apply to this operation. This includes main context
     * (e.g. "/app/docs/update") as well as any matching wildcard contexts
     * (e.g. "/app/docs/*")
     */
    protected List<Context> contexts = new ArrayList<>();

    /** Transaction factory */
    protected Supplier<? extends Context> contextFactory = Context.FACTORY;

    /**
     * Configuration indexes for performance.
     */
    protected ConfigIndexes configIndexes = Creator.create(ConfigIndexes.class);

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

    /* Attributes */
    public String name()
    {
        return name;
    }

    public void name(String name)
    {
        this.name = name;
    }

    public List<Context> contexts()
    {
        return contexts;
    }

    public ConfigIndexes indexes()
    {
        return configIndexes;
    }
}
