package com.taitl.existential.handlers.access_handlers;

import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

import java.util.function.*;

/**
 * Event handler for Write events.
 *
 * @param <T>
 *            Type of entity being written
 */
public class OnWrite<T> extends On<T> implements UniEventHandler<T>
{
    /**
     * Creates a write handler with a description.
     *
     * @param action
     *            Action to invoke when the write occurs
     * @param description
     *            Human-friendly description of the handler
     */
    public OnWrite(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    /**
     * Creates a conditional write handler.
     *
     * @param condition
     *            Predicate that decides whether the handler runs
     * @param action
     *            Action to invoke when the write occurs
     */
    public OnWrite(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    /**
     * Creates a conditional write handler with a description.
     *
     * @param condition
     *            Predicate that decides whether the handler runs
     * @param action
     *            Action to invoke when the write occurs
     * @param description
     *            Human-friendly description of the handler
     */
    public OnWrite(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Write.class);
    }
}
