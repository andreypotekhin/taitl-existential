package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnReadAndLock<T> extends On<T>
{
    public OnReadAndLock(Consumer<? super T> action)
    {
        super(action);
    }

    public OnReadAndLock(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
