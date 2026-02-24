package com.taitl.existential.handlers.access_handlers;

import java.util.function.*;
import com.taitl.existential.handlers.*;

/**
 * Declares a handler that runs when an entity is changed.
 *
 * @param <T>
 *            Type of entity changed
 */
public class OnChange<T> extends On<T>
{
    public OnChange(Consumer<? super T> action)
    {
        super(action);
    }

    public OnChange(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnChange(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnChange(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
