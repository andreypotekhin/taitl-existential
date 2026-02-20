package com.taitl.existential.handlers.access_handlers;

import java.util.function.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

/**
 * Declarative handler for {@link Change} events.
 *
 * <p>Extends {@link On} to describe conditional actions that run after a
 * change is applied to an entity.</p>
 *
 * @param <T>
 *            Type of entity changed
 */
public class OnChange<T> extends On<T> implements EventHandlerWithSideEffects<T>
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
