package com.taitl.existential.handlers.access_handlers;

import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;

import java.util.function.*;

/**
 * Declares a handler that runs when an entity is changed.
 *
 * @param <T>
 *            Type of entity changed
 * @deprecated Use OnUpdate (rationale: same functionality)
 */
@Deprecated(since = "2026-03", forRemoval = true)
public class OnChange<T> extends On<T>
{

    public OnChange(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnChange(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnChange(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Change.class);
    }
}
