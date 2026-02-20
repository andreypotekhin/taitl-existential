package com.taitl.ex.logic.configuration.indexes.data;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Maps event key to a set of configured event handlers/expressions (Evs)
 * Example: E<Doc<JSON>> -> Set<On[Е]<Doc<JSON>>>
 * where E is one of Create, Update, Delete, Read, Write, Mutate, Transit.
 *
 * Example:
 *   To retrieve the event handlers/expressions defined for the type "Create<Doc<JSON>>":
 *   Set<EventHandler> handlers = evHandlers.get("Create<Doc<JSON>>")
 */
public class ConfiguredHandlers
{
    // EventKey to Set<EventHandler<>>
    Multimap<String, Ev<?>> handlers = new Multimap<>();

    /**
     * Gets event handlers for the specified event key.
     *
     * @param key
     *            TypeKey to search for.
     * @return Set<Ev<T>>, or null if no handlers defined for the type.
     */
    public Set<Ev<?>> get(EventKey key)
    {
        sane(key, "key");
        Set<Ev<?>> result = handlers.get(key.toString());
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

    public <T> Set<Ev<?>> put(EventKey key, Ev<T> value)
    {
        sane(key, "key");
        sane(value, "value");
        return handlers.put(key.toString(), value);
    }

    public void clear()
    {
        handlers.clear();
    }
}
