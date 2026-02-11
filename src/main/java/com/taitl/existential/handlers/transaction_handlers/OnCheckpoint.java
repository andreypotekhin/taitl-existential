package com.taitl.existential.handlers.transaction_handlers;

import java.util.function.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.transactions.*;

public class OnCheckpoint<T extends Transaction> extends On<T>
{
    public OnCheckpoint(Consumer<? super T> action)
    {
        super(action);
    }

    public OnCheckpoint(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnCheckpoint(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnCheckpoint(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
