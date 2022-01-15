package com.taitl.existential.event_handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnUpsert<T> extends On<T>
{
    public OnUpsert(Consumer<? super T> action)
    {
        super(action);
    }

    public OnUpsert(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
