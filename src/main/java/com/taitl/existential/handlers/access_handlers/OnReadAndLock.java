package com.taitl.existential.handlers.access_handlers;

import java.util.function.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.handlers.*;

/**
 * Declarative handler for {@link ReadAndLock} access events.
 * Extends {@link On} to capture conditional actions that run when an
 * entity is read and locked.
 *
 * @param <T>
 *            Type of entity read and locked
 */
public class OnReadAndLock<T> extends On<T>
{
    /**
     * Creates a read-and-lock handler that always executes.
     *
     * @param action
     *            Action to invoke when the read-and-lock occurs
     */
    public OnReadAndLock(Consumer<? super T> action)
    {
        super(action);
    }

    /**
     * Creates a read-and-lock handler with a description.
     *
     * @param action
     *            Action to invoke when the read-and-lock occurs
     * @param description
     *            Human-friendly description of the handler
     */
    public OnReadAndLock(Consumer<? super T> action, String description)
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
    public OnReadAndLock(Predicate<? super T> condition, Consumer<? super T> action)
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
    public OnReadAndLock(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
