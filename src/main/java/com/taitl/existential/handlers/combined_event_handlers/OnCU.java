package com.taitl.existential.handlers.combined_event_handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

public class OnCU<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnCU(Consumer<? super T> action)
    {
        super(action);
    }

    public OnCU(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnCU(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }

    public OnCU(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
