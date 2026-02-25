package com.taitl.existential.handlers.combined_event_handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

/**
 * Handles create and update events for a given entity type.
 *
 * @param <T>
 *            Entity type
 */
public class OnCU<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    /**
     * Creates a handler that always runs for create and update events.
     *
     * @param action
     *            Handler action
     */
    public OnCU(Consumer<? super T> action)
    {
        super(action);
    }

    /**
     * Creates a handler that always runs for create and update events.
     *
     * @param action
     *            Handler action
     * @param description
     *            Human-readable description
     */
    public OnCU(Consumer<? super T> action, String description)
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
    public OnCU(Predicate<? super T> condition, Consumer<? super T> action)
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
    public OnCU(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(CU.class);
    }
}
