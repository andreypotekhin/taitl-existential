package com.taitl.existential.event_handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnUpdate<T> extends On<T>
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
