package com.taitl.ex.logic.unused;

import com.taitl.existential.configs.Transaction;

public class BeginTran<T extends Transaction> extends TransactionEvent<T>
{
    public BeginTran(T tr)
    {
        super(tr);
    }
}
