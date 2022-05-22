package com.taitl.existential.events;

import com.taitl.existential.event.types.TransactionEvent;
import com.taitl.existential.transactions.Transaction;

public class BeginTran<Tr extends Transaction> extends TransactionEvent<Tr>
{
    public BeginTran(Tr tr)
    {
        super(tr);
    }
}
