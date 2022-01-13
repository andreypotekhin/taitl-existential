package com.taitl.existential.events;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnMutate<T> extends On<T>
{
    public OnMutate(Consumer<? super T> action)
    {
        super(action);
    }

    public OnMutate(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
