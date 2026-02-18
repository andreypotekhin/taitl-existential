package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;
import com.taitl.existential.events.*;
import com.taitl.existential.handlers.types.*;

/**
 * Declarative handler for {@link Create} events.
 *
 * <p>Extends {@link On} to capture an optional condition, an action, and a
 * human-friendly description for entity creation.</p>
 *
 * @param <T>
 *            Type of entity created
 */
public class OnCreate<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnCreate(Consumer<? super T> action)
    {
        super(action);
    }

    public OnCreate(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnCreate(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnCreate(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
