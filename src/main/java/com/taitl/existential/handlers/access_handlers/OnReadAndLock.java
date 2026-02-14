package com.taitl.existential.handlers.access_handlers;

import java.util.function.*;
import com.taitl.existential.handlers.*;

public class OnReadAndLock<T> extends On<T>
{
    public OnReadAndLock(Consumer<? super T> action)
    {
        super(action);
    }

    public OnReadAndLock(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnReadAndLock(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }

    public OnReadAndLock(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
