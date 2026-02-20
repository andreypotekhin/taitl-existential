package com.taitl.existential.handlers.combined_event_handlers;

import java.util.function.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

public class OnCUD<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnCUD(Consumer<? super T> action)
    {
        super(action);
    }

    public OnCUD(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnCUD(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }

    public OnCUD(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
