package com.taitl.existential.handlers;

import java.util.function.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;

/**
 * Event handler for delete operations.
 *
 * @param <T>
 *            Entity type handled by the delete event
 */
public class OnDelete<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnDelete(Consumer<? super T> action)
    {
        super(action);
    }

    public OnDelete(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnDelete(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnDelete(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Delete.class);
    }
}
