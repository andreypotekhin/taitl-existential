package com.taitl.existential.contexts;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.taitl.existential.EventSplitter;
import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.Multimap;
import com.taitl.existential.helper.State;
import com.taitl.existential.keys.OpKey;

public class Contexts
{
    /**
     * Known contexts. New contexts are created by call to getcreate().
     */
    protected static Multimap<String, Context> contexts = new Multimap<>();

    /* Factories for customizing the contexts. */

    /**
     * Context factory.
     */
    protected static Supplier<? extends Context> contextFactory = () -> new Context("unknown");

    /**
     * EventSplitter factory.
     */
    protected static Supplier<? extends EventSplitter> eventSplitterFactory =
            () -> new EventSplitter();

    /**
     * Get, or create if missing, contexts by business operation name.
     * Operation name is a non-wildcarded one, e.g. "/app/flights/update"
     *
     * When wildcard contexts are defined, multiple contexts may match
     * single operation name: "/app/flights/update", "/app/flights", "/app/*"
     *
     * Create new context if no matching context exist. Create all parent
     * contexts if context is not a root content (/).
     *
     * Example: getcreate("/app/flights/update") will create these three contexts:
     * "/app/flights/update"
     * "/app/flights"
     * "/app"
     * of which it will return the top one ("/app/flights/update")
     *
     * TODO: extend to retrieve wildcard contexts
     */
    public static List<Context> getcreate(String op)
    {
        Args.cool(op, "op");
        OpKey opk = new OpKey(op);
        Set<Context> result = contexts.get(op);
        if (result == null || result.isEmpty())
        {
            Context context = contextFactory.get();
            context.name = op;
            if (opk.hasParent())
            {
                List<Context> parents = getcreate(opk.getParent().toString());
                State.verify(parents.size() <= 1, "A context can only have zero or one parent");
                if (!parents.isEmpty())
                {
                    context.parent = parents.get(0);
                }
            }
            synchronized (contexts)
            {
                result = contexts.get(op);
                if (result == null || result.isEmpty())
                {
                    result = contexts.put(op, context);
                }
            }
        }
        State.verify(result != null, "result member should not be null");
        return result.stream().collect(Collectors.toList());
    }

    /* Factories for customizing the contexts. */

    public static void contextFactory(Supplier<? extends Context> supplier)
    {
        Args.cool(supplier, "supplier");
        contextFactory = supplier;
    }

    public static void eventSplitter(Supplier<? extends EventSplitter> supplier)
    {
        Args.cool(supplier, "supplier");
        eventSplitterFactory = supplier;
    }
}
