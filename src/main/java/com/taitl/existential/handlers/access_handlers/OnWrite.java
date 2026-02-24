package com.taitl.existential.handlers.access_handlers;

import java.util.function.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

/**
 * Declares a handler that runs when an entity is written.
 *
 * @param <T>
 *            Type of entity being written
 */
public class OnWrite<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    /**
     * Creates a write handler that always executes.
     *
     * @param action
     *            Action to invoke when the write occurs
     */
    public OnWrite(Consumer<? super T> action)
    {
        super(action);
    }

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
}
