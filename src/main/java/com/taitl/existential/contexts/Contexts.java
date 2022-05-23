package com.taitl.existential.contexts;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

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
     * Transaction factory.
     */
    // static Supplier<? extends Transaction> transactionFactory = () -> new Transaction();

    /**
     * Context factory.
     */
    static Supplier<? extends Context> contextFactory = () -> new Context("unknown");

    /**
     * EventSplitter factory.
     */
    static Supplier<? extends EventSplitter> eventSplitterFactory = () -> new EventSplitter();

    /**
     * Get contexts by business operation name.
     * When using wildcard contexts, multiple contexts may exist for single operation name:
     * TODO: provide more details
     *
     * Create new context if no matching context exist. Create parent contexts if
     * context is not a root content (/).
     *
     * Example: getcreate("/app/flights/update") will create three contexts:
     * "/app/flights/update"
     * "/app/flights"
     * "/app"
     *
     * TODO: extend code to retrieve wildcard contexts as well
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
        return result.stream().toList();
    }

    /* Factories for customizing the contexts. */

    /*
     * public static void transactionFactory(Supplier<? extends Transaction> supplier) {
     * Args.cool(supplier, "supplier"); transactionFactory = supplier; }
     */

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
