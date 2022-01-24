package com.taitl.existential.event_handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnWrite<T> extends On<T>
{
    public OnWrite(Consumer<? super T> action)
    {
        super(action);
    }

    public OnWrite(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
