package com.taitl.existential.handlers.combined_event_handlers;

import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

import java.util.function.*;

/**
 * Event handler for Update and Delete events.
 *
 * @param <T>
 *            Entity type
 */
public class OnUD<T> extends On<T> implements UniEventHandler<T>
{
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
