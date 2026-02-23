package com.taitl.existential.handlers.combined_event_handlers;

import java.util.function.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

/**
 * Handles create, update, and delete events for a given entity type.
 *
 * @param <T>
 *            Entity type
 */
public class OnCUD<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    /**
     * Creates a handler that always runs for create, update, and delete events.
     *
     * @param action
     *            Handler action
     */
    public OnCUD(Consumer<? super T> action)
    {
        super(action);
    }

    /**
     * Creates a handler that always runs for create, update, and delete events.
     *
     * @param action
     *            Handler action
     * @param description
     *            Human-readable description
     */
    public OnCUD(Consumer<? super T> action, String description)
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
    public OnCUD(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
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
    public OnCUD(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
