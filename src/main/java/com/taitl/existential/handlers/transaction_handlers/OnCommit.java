package com.taitl.existential.handlers.transaction_handlers;

import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.handlers.*;

public class OnCommit<T extends Transaction> extends On<T>
{
    public OnCommit(Consumer<? super T> action)
    {
        super(action);
    }

    public OnCommit(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnCommit(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnCommit(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
