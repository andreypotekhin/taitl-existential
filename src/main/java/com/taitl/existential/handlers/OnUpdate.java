package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.handler.types.EventHandlerWithSideEffects;

public class OnUpdate<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnUpdate(Consumer<? super T> action)
    {
        super(action);
    }

    public OnUpdate(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
