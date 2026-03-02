package com.taitl.existential.handlers;

import java.util.function.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;

/**
 * Declarative handler for {@link Create} events.
 * Extends {@link On} to capture an optional condition, an action, and a
 * human-friendly description for entity creation.
 *
 * @param <T>
 *            Type of entity created
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
