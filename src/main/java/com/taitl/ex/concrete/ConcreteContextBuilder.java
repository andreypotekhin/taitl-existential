package com.taitl.ex.concrete;

import com.taitl.ex.common.creator.*;
import com.taitl.existential.configs.*;

import java.util.function.*;

public class ConcreteContextBuilder
{
    protected BiFunction<String, String, ? extends Transaction> transactionFactory = Transaction.FACTORY;

    public ConcreteContext build()
    {
        ConcreteContext result = new ConcreteContext();
        result.ruleData = Creator.create(RuleData.class);
        result.transactionFactory = transactionFactory;
        return result;
    }

    public ConcreteContextBuilder transactionFactory(
            BiFunction<String, String, ? extends Transaction> transactionFactory)
    {
        this.transactionFactory = transactionFactory;
        return this;
    }

    public ConcreteContextBuilder inheritedTransactionFactory()
    {
        this.transactionFactory = null;
        return this;
    }
}
