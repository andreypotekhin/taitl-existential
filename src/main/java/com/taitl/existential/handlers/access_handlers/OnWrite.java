package com.taitl.existential.handlers.access_handlers;

import java.util.function.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

public class OnWrite<T> extends On<T> implements EventHandlerWithSideEffects<T>
{
    public OnWrite(Consumer<? super T> action)
    {
        super(action);
    }

    public OnWrite(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnWrite(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }

    public OnWrite(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
