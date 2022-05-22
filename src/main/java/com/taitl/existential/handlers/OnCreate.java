package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.handler.types.EventHandlerWithSideEffects;

public class OnCreate<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnCreate(Consumer<? super T> action)
    {
        super(action);
    }

    public OnCreate(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
