package com.taitl.existential.handlers;

import java.util.function.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;

/**
 * Event handler for update operations.
 *
 * @param <T>
 *            Entity type handled by the update event
 */
public class OnUpdate<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnUpdate(Consumer<? super T> action)
    {
        super(action);
    }

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
