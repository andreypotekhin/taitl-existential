package com.taitl.ex.logic.transactions.actions;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.transactions.*;

public class CreateTransaction
{
    public Tr forConfig(String op, Config config, Transaction custom)
    {
        return TransactionCreation.forConfig(op, config, custom, this::generateId, CreateTransaction::forContext);
    }

    public Tr forContexts(String op, List<Context> contexts, Transaction custom)
    {
        return TransactionCreation.forContexts(op, contexts, custom, this::generateId, CreateTransaction::forContext);
    }

    protected UUID generateId()
    {
        return TransactionCreation.generateId();
    }

    protected static Transaction forContext(Context context)
    {
        return TransactionCreation.forContext(context);
    }
}
