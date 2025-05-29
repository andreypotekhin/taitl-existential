package com.taitl.exlogic.unused;

import com.taitl.existential.transactions.Transaction;

public class BeginTran<T extends Transaction> extends TransactionEvent<T>
{
    public BeginTran(T tr)
    {
        super(tr);
    }
}
