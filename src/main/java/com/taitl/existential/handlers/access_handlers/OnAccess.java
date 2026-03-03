package com.taitl.existential.handlers.access_handlers;

import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;

import java.util.function.*;

/**
 * Declares a handler that runs when an entity was accessed
 * in the course of a transaction.
 *
 * @param <T>
 *            Type of entity accessed
 * @deprecated Use On (rationale: same function)
 */
@Deprecated(since = "2026-03", forRemoval = true)
public class OnAccess<T> extends On<T>
{

    public OnAccess(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnAccess(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnAccess(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Access.class);
    }
}
