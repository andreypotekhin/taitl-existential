package com.taitl.existential.handlers;

import com.taitl.existential.events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;

import java.util.function.*;

/**
 * Event handler for Delete events.
 *
 * @param <T>
 *            Entity type handled by the delete event
 *
 * @see Delete
 */
public class OnDelete<T> extends On<T> implements UniEventHandler<T>
{

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
