package com.taitl.ex.logic.indexing.data.config_indexes;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import static com.taitl.existential.constants.Strings.*;

/**
 * Maps type (e.g. T<U>) to a set of corresponding event handlers, Set<On[E]<T<U>>>
 * for which a rule is defined in a Transaction or its Contexts.
 * Example: Doc<JSON> -> Set<On[Е]<Doc<JSON>>>
 * Here, E is one of events Create, Update, Upsert, Delete, Read, Mutate, Transit.
 *
 * Example:
 *   To retrieve the event handlers defined for type "Doc<JSON>":
 *   Set<EventHandler> handlers = eventHandlers.get("Doc<JSON>")
 *
 * @see EventHandlerWithSideEffects
 * @see BiEventHandlerWithSideEffects
 */
public class EventHandlers<T>
{
    Multimap<String, EventHandler<T>> storage = new Multimap<>();

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
        Set<EventHandler<T>> result = storage.get(key.toString());
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
        return storage.containsKey(key.toString());
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
        return storage.put(key.toString(), value);
    }
}
