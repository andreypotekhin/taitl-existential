package com.taitl.existential.unused;

import java.util.Set;

import com.taitl.existential.constants.Strings;
import com.taitl.existential.event.types.BiEvent;
import com.taitl.existential.handlers.types.EventHandlerWithSideEffects;
import com.taitl.existential.helper.Multimap;

/**
 * Multimap mapping event key (Event E<T> + Type) to a set of corresponding event handlers: Set<On[E]<T>>
 * 
 * Example: Create<Doc<Json>> -> Set<OnCreate<Doc<Json>>>
 * 
 * Example: // Retrieve event handlers for eventKey 'Create<Doc<Json>>' Set<EventHandler> handlers =
 * eventHandlers.get(new EventKey(Create<Doc>.class, "Doc<Json>"))
 * 
 * @author Andrey Potekhin
 * @see EventAndTypeKey
 * @see Event
 * @see BiEvent
 *
 */
public class EventHandlers0<T>
{
    Multimap<EventAndTypeKey<T>, EventHandlerWithSideEffects<T>> storage = new Multimap<>();

    /**
     * Gets event handlers for an event key (combination of Event E<T> + Type ).
     * 
     * @param key
     *            EventKey to search for.
     * @return Set<EventHandler<>>, or null if no handlers defined for the key.
     */
    public Set<EventHandlerWithSideEffects<T>> getEventHandlers(EventAndTypeKey<T> key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(Strings.ARG_KEY);
        }
        Set<EventHandlerWithSideEffects<T>> result = storage.get(key);
        if (result != null && result.isEmpty())
        {
            result = null;
        }
        return result;
    }
}
