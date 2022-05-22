package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.transactions.Transaction;

public class OnBegin<T extends Transaction> extends On<T>
{
    public OnBegin(Consumer<? super T> action)
    {
        super(action);
    }

    public OnBegin(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(action);
    }
}
