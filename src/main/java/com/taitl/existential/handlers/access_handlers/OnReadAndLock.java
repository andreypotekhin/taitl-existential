package com.taitl.existential.handlers.access_handlers;

import java.util.function.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.handlers.*;

/**
 * Declarative handler for {@link ReadAndLock} access events.
 *
 * <p>Extends {@link On} to capture conditional actions that run when an
 * entity is read and locked.</p>
 *
 * @param <T>
 *            Type of entity read and locked
 */
public class OnReadAndLock<T> extends On<T>
{
    public OnReadAndLock(Consumer<? super T> action)
    {
        super(action);
    }

    public OnReadAndLock(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnReadAndLock(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }

    public OnReadAndLock(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
