package com.taitl.existential.handlers.access_handlers;

import java.util.function.*;
import com.taitl.existential.handlers.*;

public class OnRead<T> extends On<T>
{
    public OnRead(Consumer<? super T> action)
    {
        super(action);
    }

    public OnRead(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnRead(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }

    public OnRead(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
