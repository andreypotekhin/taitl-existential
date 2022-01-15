package com.taitl.existential.event_handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnCreate<T> extends On<T>
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
