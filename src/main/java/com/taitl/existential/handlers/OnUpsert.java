package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.handler.types.EventHandlerWithSideEffects;

public class OnUpsert<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnUpsert(Consumer<? super T> action)
    {
        super(action);
    }

    public OnUpsert(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnUpsert(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }

    public OnUpsert(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
