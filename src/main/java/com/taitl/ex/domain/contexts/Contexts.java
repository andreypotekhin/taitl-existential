package com.taitl.ex.domain.contexts;

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
    public List<Context> getContexts(String op)
    {
        Set<Context> result = allContexts.get(op);
        if (result != null && !result.isEmpty())
        {
            return new ArrayList<>(result);
        }
        return buildContexts.buildContextsRecursively(op);
    }
}
