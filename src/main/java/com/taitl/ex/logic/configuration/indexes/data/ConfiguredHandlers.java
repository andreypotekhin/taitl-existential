package com.taitl.ex.logic.configuration.indexes.data;

import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Maps event key to a set of configured event handlers/expressions (Evs)
 * Example: E<Doc<JSON>> -> Set<On[Е]<Doc<JSON>>>
 * where E is one of Create, Update, Delete, Read, Write, Mutate, Transit.
 *
 * Example:
 *   To retrieve the event handlers/expressions defined for the type "Create<Doc<JSON>>":
 *   Set<OrderlyEv> handlers = evHandlers.get("Create<Doc<JSON>>")
 */
public class ConfiguredHandlers
{
    // EventKey to Set<EventHandler<>>
    protected SetMap<String, OrderlyEv<?>> handlers = new SetMap<>();
    protected boolean ready = false;

    protected ConfigurationIndexes ci;

    public ConfiguredHandlers(ConfigurationIndexes ci)
    {
        this.ci = ci;
    }

    /**
     * Gets event handlers for the specified event key.
     *
     * @param key
     *            TypeKey to search for.
     * @return Set<Ev<T>>, or null if no handlers defined for the type.
     */
    public Set<OrderlyEv<?>> get(EventKey key)
    {
        sane(key, "key");
        Set<OrderlyEv<?>> result = handlers.get(key.toString());
        if (result != null && result.isEmpty())
        {
            result = null;
        }
        return result;
    }

    public boolean contains(EventKey key)
    {
        sane(key, "key");
        return handlers.containsKey(key.toString());
    }

    public <T> Set<OrderlyEv<?>> put(EventKey key, Ev<T> value)
    {
        sane(key, "key");
        sane(value, "value");
        check(value.single(), "Only single event handlers (Ev<T>) are supported,"
                + " but the passed-in is a compound one (like Evs<T>).");
        synchronized (this)
        {
            OrderlyEv<T> orderedValue = ci.maintainGlobalOrder.globallyOrdered(value);
            return handlers.put(key.toString(), orderedValue);
        }
    }

    /**
     * Is indexing finalized?
     */
    public boolean ready()
    {
        return ready;
    }

    public void ready(boolean ready)
    {
        this.ready = ready;
    }

    public void clear()
    {
        handlers.clear();
    }
}
