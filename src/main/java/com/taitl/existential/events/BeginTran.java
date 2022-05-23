package com.taitl.existential.events;

import com.taitl.existential.event.types.TransactionEvent;
import com.taitl.existential.transactions.Transaction;

public class BeginTran<T extends Transaction> extends TransactionEvent<T>
{
    public BeginTran(T tr)
    {
        super(tr);
    }
}
