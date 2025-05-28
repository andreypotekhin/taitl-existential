package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;
import com.taitl.existential.handlers.types.*;

public class OnModify<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnModify(Consumer<? super T> action)
    {
        super(action);
    }

    public OnModify(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnModify(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnModify(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
