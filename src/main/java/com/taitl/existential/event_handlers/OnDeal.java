package com.taitl.existential.event_handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class OnDeal<T> extends On<T>
{
    public OnDeal(Consumer<? super T> action)
    {
        super(action);
    }

    public OnDeal(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
