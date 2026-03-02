package com.taitl.ex.logic.unused;

import com.taitl.ex.common.helper.collections.SetMap;
import com.taitl.existential.events.types.BiEvent;
import com.taitl.existential.events.types.Event;
import com.taitl.existential.handlers.types.UniEventHandler;

import java.util.Set;

/**
 * Multimap mapping event key (Event E<T> + Type) to a set of corresponding event handlers: Set<On[E]<T>>
 * 
 * Example: Create<Doc<Json>> -> Set<OnCreate<Doc<Json>>>
 * 
 * Example: // Retrieve event handlers for eventKey 'Create<Doc<Json>>' Set<EventHandler> handlers =
 * eventHandlers.get(new EventKey(Create<Doc>.class, "Doc<Json>"))
 * 
 * @see EventAndTypeKey
 * @see Event
 * @see BiEvent
 *
 */
@Deprecated
public class EventHandlers0<T>
{
    SetMap<EventAndTypeKey<T>, UniEventHandler<T>> storage = new SetMap<>();

    /**
     * Gets event handlers for an event key (combination of Event E<T> + Type ).
     * 
     * @param key
     *            EventKey to search for.
     * @return Set<EventHandler<>>, or null if no handlers defined for the key.
     */
    public Set<UniEventHandler<T>> getEventHandlers(EventAndTypeKey<T> key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Argument 'key' must not be null");
        }
        Set<UniEventHandler<T>> result = storage.get(key);
        if (result != null && result.isEmpty())
        {
            result = null;
        }
        return result;
    }
}
