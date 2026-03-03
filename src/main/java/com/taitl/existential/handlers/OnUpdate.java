package com.taitl.existential.handlers;

import com.taitl.existential.events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;

import java.util.function.*;

/**
 * Event handler for Update event.
 *
 * @param <T>
 *            Entity type handled by the update event
 *
 * @see Update
 */
public class OnUpdate<T> extends On<T> implements UniEventHandler<T>
{

    public OnUpdate(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnUpdate(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnUpdate(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Update.class);
    }
}
