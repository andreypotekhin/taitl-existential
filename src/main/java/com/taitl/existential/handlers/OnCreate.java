package com.taitl.existential.handlers;

import com.taitl.existential.events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;

import java.util.function.*;

/**
 * Event handler for Create events.
 *
 * @param <T>
 *            Type of entity created
 *
 * @see Create
 */
public class OnCreate<T> extends On<T> implements UniEventHandler<T>
{

    public OnCreate(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnCreate(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnCreate(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Create.class);
    }
}
