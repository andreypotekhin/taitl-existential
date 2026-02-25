package com.taitl.existential.handlers.combined_event_handlers;

import java.util.function.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

/**
 * Handles update and delete events for a given entity type.
 *
 * @param <T>
 *            Entity type
 */
public class OnUD<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    /**
     * Creates a handler that always runs for update and delete events.
     *
     * @param action
     *            Handler action
     */
    public OnUD(Consumer<? super T> action)
    {
        super(action);
    }

    /**
     * Creates a handler that always runs for update and delete events.
     *
     * @param action
     *            Handler action
     * @param description
     *            Human-readable description
     */
    public OnUD(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    /**
     * Creates a handler that runs when the condition is met.
     *
     * @param condition
     *            Condition to check before running
     * @param action
     *            Handler action
     */
    public OnUD(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    /**
     * Creates a handler that runs when the condition is met.
     *
     * @param condition
     *            Condition to check before running
     * @param action
     *            Handler action
     * @param description
     *            Human-readable description
     */
    public OnUD(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(UD.class);
    }
}
