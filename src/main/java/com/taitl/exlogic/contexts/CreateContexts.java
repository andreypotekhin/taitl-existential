package com.taitl.exlogic.contexts;

import java.util.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.creator.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;

public class CreateContexts
{
    protected Contexts parent;

    public CreateContexts(Contexts contexts)
    {
        this.parent = contexts;
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
     * Move method to ContextFactory
     * Extend to retrieve wildcard contexts
     */
    public List<Context> createContextsRecursively(String op)
    {
        Args.cool(op, "op");
        OpKey opKey = new OpKey(op);
        Set<Context> result = parent.allContexts.get(op);
        if (result == null || result.isEmpty())
        {
            Context context = Creator.create(Context.class, new Class[] { String.class }, op);
            if (opKey.hasParent())
            {
                List<Context> parents = createContextsRecursively(opKey.getParent().toString());
                State.verify(parents.size() <= 1, // Does this make sense?
                        "A context can only have zero or one parent");
                if (!parents.isEmpty())
                {
                    context.parent(parents.get(0));
                }
            }
            synchronized (this)
            {
                result = parent.allContexts.get(op);
                if (result == null || result.isEmpty())
                {
                    result = parent.allContexts.put(op, context);
                }
            }
        }
        State.verify(result != null, "result member should not be null");
        return new ArrayList<>(result);
    }
}
