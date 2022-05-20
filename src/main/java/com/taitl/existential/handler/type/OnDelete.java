package com.taitl.existential.handler.type;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.handler.base.EventHandlerWithSideEffects;

public class OnDelete<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnDelete(Consumer<? super T> action)
    {
        super(action);
    }

    public OnDelete(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
