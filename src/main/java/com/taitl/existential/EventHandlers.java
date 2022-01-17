package com.taitl.existential;

import static com.taitl.existential.constants.Strings.KEY_ARG;
import static com.taitl.existential.constants.Strings.VALUE_ARG;

import java.util.Set;

import com.taitl.existential.commons.Multimap;
import com.taitl.existential.constants.Strings;
import com.taitl.existential.interfaces.EntityHandler;
import com.taitl.existential.interfaces.EventHandler;
import com.taitl.existential.interfaces.MutationHandler;

/**
 * Multimap mapping of a type (e.g. T<U>) to a set of corresponding event handlers, Set<On[E]<T<U>>>
 * 
 * Example: Doc<Json> -> Set<On[Е]<Doc<Json>>>
 * 
 * Example: 
 *   Retrieve event handlers defined for type "Doc<Json>":
 *   Set<Handler> handlers = eventHandlers.get("Doc<Json>")
 * 
 * @author Andrey Potekhin
 * @see EntityHandler
 * @see MutationHandler
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
            throw new IllegalArgumentException(Strings.ARG_KEY);
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
            throw new IllegalArgumentException(KEY_ARG);
        }
        return storage.containsKey(key.toString());
    }

    public Set<EventHandler<T>> put(TypeKey<T> key, EventHandler<T> value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(KEY_ARG);
        }
        if (value == null)
        {
            throw new IllegalArgumentException(VALUE_ARG);
        }
        return storage.put(key.toString(), value);
    }
}
