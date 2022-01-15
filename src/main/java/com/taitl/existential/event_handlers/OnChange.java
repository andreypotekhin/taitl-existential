package com.taitl.existential.event_handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnChange<T> extends On<T>
{
    public OnChange(Consumer<? super T> action)
    {
        super(action);
    }

    public OnChange(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
