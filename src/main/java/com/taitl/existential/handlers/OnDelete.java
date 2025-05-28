package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;
import com.taitl.existential.handlers.types.*;

public class OnDelete<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnDelete(Consumer<? super T> action)
    {
        super(action);
    }

    public OnDelete(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnDelete(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnDelete(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
