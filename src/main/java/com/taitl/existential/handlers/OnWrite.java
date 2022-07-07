package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.handler.types.EventHandlerWithSideEffects;

public class OnWrite<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnWrite(Consumer<? super T> action)
    {
        super(action);
    }

    public OnWrite(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnWrite(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }

    public OnWrite(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
