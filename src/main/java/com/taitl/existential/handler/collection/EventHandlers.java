package com.taitl.existential.handler.collection;

import static com.taitl.existential.constants.Strings.ARG_KEY;
import static com.taitl.existential.constants.Strings.ARG_VALUE;

import java.util.Set;

import com.taitl.existential.handler.types.BiEventHandlerWithSideEffects;
import com.taitl.existential.handler.types.EventHandler;
import com.taitl.existential.handler.types.EventHandlerWithSideEffects;
import com.taitl.existential.helper.Multimap;
import com.taitl.existential.keys.TypeKey;

/**
 * A multimap mapping of a type (e.g. T<U>) to a set of corresponding event handlers, Set<On[E]<T<U>>>
 *
 * Example: Doc<Json> -> Set<On[Е]<Doc<Json>>>
 *
 * Example:
 *   Retrieve event handlers defined for type "Doc<Json>":
 *   Set<Handler> handlers = eventHandlers.get("Doc<Json>")
 *
 * @author Andrey Potekhin
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
