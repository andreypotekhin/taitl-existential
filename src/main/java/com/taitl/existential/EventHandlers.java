package com.taitl.existential;

import java.util.Set;

import com.taitl.existential.commons.Multimap;
import com.taitl.existential.constants.Strings;
import com.taitl.existential.events.BiEvent;
import com.taitl.existential.interfaces.EventHandler;

/**
 * Multimap mapping event key (Event E<T> + Type) to a set of corresponding event handlers: Set<On[E]<T>>
 * 
 * Example: Create<Doc<Json>> -> Set<OnCreate<Doc<Json>>>
 * 
 * Example: // Retrieve event handlers for eventKey 'Create<Doc<Json>>' Set<EventHandler> handlers =
 * eventHandlers.get(new EventKey(Create<Doc>.class, "Doc<Json>"))
 * 
 * @author Andrey Potekhin
 * @see EventKey
 * @see Event
 * @see BiEvent
 *
 */
public class EventHandlers<T>
{
    Multimap<EventKey<T>, EventHandler<T>> storage = new Multimap<>();

    /**
     * Gets event handlers for an event key (combination of Event E<T> + Type ).
     * 
     * @param key
     *            EventKey to search for.
     * @return Set<EventHandler<>>, or null if no handlers defined for the key.
     */
    public Set<EventHandler<T>> getEventHandlers(EventKey<T> key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(Strings.ARG_KEY);
        }
        Set<EventHandler<T>> result = storage.get(key);
        if (result != null && result.isEmpty())
        {
            result = null;
        }
        return result;
    }
}
