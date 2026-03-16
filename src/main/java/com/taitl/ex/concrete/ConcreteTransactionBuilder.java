package com.taitl.ex.concrete;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.indexes.*;
import com.taitl.existential.configs.*;

public class ConcreteTransactionBuilder
{
    protected Transaction transaction;

    public ConcreteTransaction build()
    {
        ConcreteTransaction result = new ConcreteTransaction();
        result.ruleData = Creator.create(RuleData.class);
        result.indexes = Creator.create(TransactionIndexes.class, new Class[] { Transaction.class }, transaction);
        return result;
    }

    public ConcreteTransactionBuilder transaction(Transaction transaction)
    {
        this.transaction = transaction;
        return this;
    }
}
