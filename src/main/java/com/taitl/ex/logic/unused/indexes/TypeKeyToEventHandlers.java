package com.taitl.ex.logic.unused.indexes;

import com.taitl.ex.common.helper.collections.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import java.util.*;

/**
 * Maps entity type to a set of configured event handlers, Set<On[E]<T<U>>>
 * Example: Doc<JSON> -> Set<On[Е]<Doc<JSON>>>
 * where E is one of Create, Update, Upsert, Delete, Read, Transit, Port.
 *
 * Example:
 *   To retrieve the event handlers defined for the type "Doc<JSON>":
 *   Set<EventHandler> handlers = eventHandlers.get("Doc<JSON>")
 */
@Deprecated
public class TypeKeyToEventHandlers<T>
{
    SetMap<String, EventHandler<T>> handlers = new SetMap<>();

    /**
     * Gets event handlers for the specified type.
     *
     * @param key
     *            TypeKey to search for.
     * @return Set<EventHandler<T>>, or null if no handlers defined for the type.
     */
    public Set<EventHandler<T>> get(TypeKey<T> key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Argument 'key' must not be null");
        }
        Set<EventHandler<T>> result = handlers.get(key.toString());
        if (result != null && result.isEmpty())
        {
            result = null;
        }
        return result;
    }

    public boolean contains(TypeKey<T> key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Argument 'key' must not be null");
        }
        return handlers.containsKey(key.toString());
    }

    public Set<EventHandler<T>> put(TypeKey<T> key, EventHandler<T> value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Argument 'key' must not be null");
        }
        if (value == null)
        {
            throw new IllegalArgumentException("Argument 'value' must not be null");
        }
        return handlers.add(key.toString(), value);
    }

    public void clear()
    {
        handlers.clear();
    }
}
