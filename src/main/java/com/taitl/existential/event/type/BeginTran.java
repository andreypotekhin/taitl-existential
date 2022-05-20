package com.taitl.existential.event.type;

import com.taitl.existential.event.base.TransactionEvent;
import com.taitl.existential.transactions.Transaction;

public class BeginTran<T extends Transaction> extends TransactionEvent<T>
{
    public BeginTran(T t)
    {
        super(t);
    }
}
