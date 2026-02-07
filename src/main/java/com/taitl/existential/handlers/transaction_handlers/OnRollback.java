package com.taitl.existential.handlers.transaction_handlers;

import java.util.function.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.transactions.*;

public class OnRollback<T extends Transaction> extends On<T>
{
    public OnRollback(Consumer<? super T> action)
    {
        super(action);
    }

    public OnRollback(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnRollback(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnRollback(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
