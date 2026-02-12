package com.taitl.ex.logic.configuration;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.ex.logic.configuration.actions.*;
import com.taitl.existential.contexts.*;

public class Contexts
{
    /**
     * All known contexts, keyed by op name.
     * New contexts are created by call to createContexts().
     */
    public Multimap<String, Context> allContexts = new Multimap<>();

    protected BuildContexts buildContexts = new BuildContexts(this);

    /**
     * Get, or create if missing, the contexts for business operation name.
     * Operation name is a non-wildcarded, for instance, "/app/flights/update"
     * When parent or wildcard contexts are defined, multiple contexts may match
     * a single operation: "/app/flights/update", "/app/flights", "/app/*"
     * Creates a new Context object if no matching context already exist.
     * Creates all parent Context object if this context is not a root context (/).
     *
     * Example: createContexts("/app/flights/update") will create these three contexts,
     * tied by parent-child relationship:
     * "/app/flights/update"
     * "/app/flights"
     * "/app"
     * "/"
     * of which it will return the top one ("/app/flights/update")
     */
    public List<Context> getContexts(String op)
    {
        Set<Context> result = allContexts.get(op);
        if (result != null && !result.isEmpty())
        {
            return new ArrayList<>(result);
        }
        return buildContexts.buildRecursively(op);
    }
}
