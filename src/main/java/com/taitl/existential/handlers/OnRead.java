package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnRead<T> extends On<T>
{
    public OnRead(Consumer<? super T> action)
    {
        super(action);
    }

    public OnRead(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
