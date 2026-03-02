package com.taitl.ex.logic.unused.indexes;

import com.taitl.ex.common.helper.collections.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Maps event key to a set of configured event handlers, Set<On[E]<T<U>>>
 * Example: E<Doc<JSON>> -> Set<On[Е]<Doc<JSON>>>
 * where E is one of Create, Update, Delete, Read, Write, Mutate, Port.
 *
 * Example:
 *   To retrieve the event handlers defined for the type "Create<Doc<JSON>>":
 *   Set<EventHandler> handlers = eventHandlers.get("Create<Doc<JSON>>")
 */
@Deprecated
public class ConfiguredEventHandlers
{
    // EventKey to Set<EventHandler<>>
    SetMap<String, EventHandler<?>> handlers = new SetMap<>();

    /**
     * Gets event handlers for the specified event key.
     *
     * @param key
     *            TypeKey to search for.
     * @return Set<EventHandler<T>>, or null if no handlers defined for the type.
     */
    public <T> Set<EventHandler<?>> get(EventKey<T> key)
    {
        sane(key, "key");
        Set<EventHandler<?>> result = handlers.get(key.toString());
        if (result != null && result.isEmpty())
        {
            result = null;
        }
        return result;
    }

    public <T> boolean contains(EventKey<T> key)
    {
        sane(key, "key");
        return handlers.containsKey(key.toString());
    }

    public <T> Set<EventHandler<?>> put(EventKey<T> key, EventHandler<T> value)
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
