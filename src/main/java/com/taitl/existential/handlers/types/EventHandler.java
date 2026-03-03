package com.taitl.existential.handlers.types;

import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.interfaces.*;

/**
 * Base interface for event handlers with side effects, such as OnUpdate<Entity>, OnTransit<Entity>.
 *
 * @param <T>
 *            Type of entity
 */
public interface EventHandler<T> extends Ev<T>, Describable
{
    default EventType eventType()
    {
        return EventType.valueOf(Event.class);
    }
}
