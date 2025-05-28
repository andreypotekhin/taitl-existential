package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;
import com.taitl.existential.handlers.types.*;

public class OnCreate<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnCreate(Consumer<? super T> action)
    {
        super(action);
    }

    public OnCreate(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnCreate(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnCreate(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
