package com.taitl.ex.logic.transactions.actions;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.transactions.*;

/**
 * Commit transaction object.
 */
public class CreateTran extends TransactionActionSupport
{
    public CreateTran(TransactionLogic tl)
    {
        super(tl);
    }

    // public Tr call(String op, Transaction custom) throws ExistentialException
    // {
    // sane(op, "op");
    // OpKey.validate(op);
    // verify(tl.ex().configured(), "You should call configs().done() first");
    // //return transactionLogic.registry().create(op, custom);
    // }

    public Tr forConfig(String op, Config config, Transaction custom)
    {
        return TransactionCreation.forConfig(op, config, custom, this::generateId, CreateTran::forContext);
    }

    public Tr forContexts(String op, List<Context> contexts, Transaction custom)
    {
        return TransactionCreation.forContexts(op, contexts, custom, this::generateId, CreateTran::forContext);
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
