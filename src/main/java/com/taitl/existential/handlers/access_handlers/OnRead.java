package com.taitl.existential.handlers.access_handlers;

import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;

import java.util.function.*;

/**
 * Event handler for Read events.
 *
 * @param <T>
 *            Type of entity being read
 */
public class OnRead<T> extends On<T>
{
    /**
     * Creates a read handler with a description.
     *
     * @param action
     *            Action to invoke when the read occurs
     * @param description
     *            Human-friendly description of the handler
     */
    public OnRead(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    /**
     * Creates a conditional read handler.
     *
     * @param condition
     *            Predicate that decides whether the handler runs
     * @param action
     *            Action to invoke when the read occurs
     */
    public OnRead(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    /**
     * Creates a conditional read handler with a description.
     *
     * @param condition
     *            Predicate that decides whether the handler runs
     * @param action
     *            Action to invoke when the read occurs
     * @param description
     *            Human-friendly description of the handler
     */
    public OnRead(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Read.class);
    }
}
