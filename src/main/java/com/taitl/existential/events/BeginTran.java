package com.taitl.existential.events;

import com.taitl.existential.events.base.TransactionEvent;
import com.taitl.existential.transactions.Transaction;

public class BeginTran<T extends Transaction> extends TransactionEvent<T>
{
    public BeginTran(T t)
    {
        super(t);
    }
}