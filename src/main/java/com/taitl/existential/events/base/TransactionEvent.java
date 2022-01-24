package com.taitl.existential.events.base;

import com.taitl.existential.constants.Strings;
import com.taitl.existential.transactions.Transaction;

public class TransactionEvent<T extends Transaction> implements Event<T>
{
    public T t;

    public TransactionEvent(T t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException(Strings.ARG_T);
        }
        this.t = t;
    }
}
