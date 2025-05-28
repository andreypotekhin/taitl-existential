package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;
import com.taitl.existential.handlers.types.*;

public class OnChange<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnChange(Consumer<? super T> action)
    {
        super(action);
    }

    public OnChange(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnChange(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnChange(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
