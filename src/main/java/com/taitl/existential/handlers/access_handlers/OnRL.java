package com.taitl.existential.handlers.access_handlers;

import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;

import java.util.function.*;

/**
 * Event handler for read-and-lock events.
 *
 * @param <T>
 *            Type of entity read and locked
 */
public class OnRL<T> extends On<T>
{
    /**
     * Creates a read-and-lock handler with a description.
     *
     * @param action
     *            Action to invoke when the read-and-lock occurs
     * @param description
     *            Human-friendly description of the handler
     */
    public OnRL(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    /**
     * Creates a conditional read-and-lock handler.
     *
     * @param condition
     *            Predicate that decides whether the handler runs
     * @param action
     *            Action to invoke when the read-and-lock occurs
     */
    public OnRL(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    /**
     * Creates a conditional read-and-lock handler with a description.
     *
     * @param condition
     *            Predicate that decides whether the handler runs
     * @param action
     *            Action to invoke when the read-and-lock occurs
     * @param description
     *            Human-friendly description of the handler
     */
    public OnRL(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(RL.class);
    }
}
