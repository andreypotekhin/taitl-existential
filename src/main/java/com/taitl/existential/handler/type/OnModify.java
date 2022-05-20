package com.taitl.existential.handler.type;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.handler.base.EventHandlerWithSideEffects;

public class OnModify<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnModify(Consumer<? super T> action)
    {
        super(action);
    }

    public OnModify(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
