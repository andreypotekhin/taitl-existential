package com.taitl.ex.logic.configuration.indexes.data;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import static com.taitl.existential.constants.Strings.*;

/**
 * Maps entity type to a set of configured event handlers, Set<On[E]<T<U>>>
 * Example: Doc<JSON> -> Set<On[Е]<Doc<JSON>>>
 * where E is one of Create, Update, Upsert, Delete, Read, Mutate, Transit.
 *
 * Example:
 *   To retrieve the event handlers defined for the type "Doc<JSON>":
 *   Set<EventHandler> handlers = eventHandlers.get("Doc<JSON>")
 */
public class TypeKeyToEventHandlers<T>
{
    Multimap<String, EventHandler<T>> handlers = new Multimap<>();

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
            throw new IllegalArgumentException(ARG_KEY);
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
            throw new IllegalArgumentException(ARG_KEY);
        }
        return handlers.containsKey(key.toString());
    }

    public Set<EventHandler<T>> put(TypeKey<T> key, EventHandler<T> value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (value == null)
        {
            throw new IllegalArgumentException(ARG_VALUE);
        }
        return handlers.put(key.toString(), value);
    }

    public void clear()
    {
        handlers.clear();
    }
}
