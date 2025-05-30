package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;
import com.taitl.existential.handlers.types.*;

public class OnUpdate<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnUpdate(Consumer<? super T> action)
    {
        super(action);
    }

    public OnUpdate(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnUpdate(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }

    public OnUpdate(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
