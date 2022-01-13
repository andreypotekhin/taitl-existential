package com.taitl.existential.events;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnDelete<T> extends On<T>
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
