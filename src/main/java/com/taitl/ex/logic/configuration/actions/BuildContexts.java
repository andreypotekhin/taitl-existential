package com.taitl.ex.logic.configuration.actions;

import java.util.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class BuildContexts
{
    protected ConfigurationLogic cl;

    public BuildContexts(ConfigurationLogic cl)
    {
        this.cl = cl;
    }

    /**
     * Get, or create if missing, the contexts for business operation.
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
    public List<Context> call(String op)
    {
        Set<Context> result = cl.contexts.get(op);
        if (result != null && !result.isEmpty())
        {
            return new ArrayList<>(result);
        }
        return buildRecursively(op);
    }

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
     * TODO:
     * Extend to retrieve wildcard contexts
     */
    protected List<Context> buildRecursively(String op)
    {
        sane(op, "op");
        OpKey opKey = new OpKey(op);
        Set<Context> result = cl.contexts.get(op);
        if (result == null || result.isEmpty())
        {
            Context context = Creator.create(Context.class, new Class[] { String.class }, op);
            verify(context.name().equals(op), "Context name should be the same as op");
            if (opKey.hasParent())
            {
                List<Context> parents = buildRecursively(opKey.getParent().toString());
                verify(parents.size() <= 1, // Does this make sense?
                        "A context can only have zero or one parent");
                if (!parents.isEmpty())
                {
                    context.parent(parents.get(0));
                }
            }
            synchronized (this)
            {
                result = cl.contexts.get(op);
                if (result == null || result.isEmpty())
                {
                    result = cl.contexts.add(op, context);
                }
            }
        }
        verify(result != null, "result member should not be null");
        return new ArrayList<>(result);
    }
}
