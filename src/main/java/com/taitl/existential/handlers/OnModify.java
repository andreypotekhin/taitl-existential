package com.taitl.existential.handlers;

import com.taitl.existential.events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;

import java.util.function.*;

/**
 * Event handler for modify operations.
 *
 * @param <T>
 *            Entity type handled by the modify event
 * @deprecated Use OnUpdate (rationale: same functionality)
 */
@Deprecated(since = "2026-03", forRemoval = true)
public class OnModify<T> extends On<T> implements UniEventHandler<T>
{

    public OnModify(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnModify(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnModify(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Modify.class);
    }
}
