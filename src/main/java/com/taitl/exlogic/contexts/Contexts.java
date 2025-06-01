package com.taitl.exlogic.contexts;

import java.util.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.creator.*;
import com.taitl.existential.helper.*;

public class Contexts
{
    /**
     * Known contexts. New contexts are created by call to createContexts().
     */
    protected Multimap<String, Context> allContexts = new Multimap<>();

    protected CreateContexts createContexts = Creator.create(CreateContexts.class,
            new Class[] { Contexts.class }, this);

    /**
     * Get, or create if missing, the contexts for business operation name.
     * Operation name is a non-wildcarded, for instance, "/app/flights/update"
     * When wildcard contexts are defined, multiple contexts may match
     * a single operation: "/app/flights/update", "/app/flights", "/app/*"
     * Create the new context if no matching context exist.
     * Create all parent contexts if context is not a root content (/).
     *
     * Example: createContexts("/app/flights/update") will create these three contexts:
     * "/app/flights/update"
     * "/app/flights"
     * "/app"
     * "/"
     * of which it will return the top one ("/app/flights/update")
     */
    public List<Context> createContexts(String op)
    {
        return createContexts.createContextsRecursively(op);
    }
}
