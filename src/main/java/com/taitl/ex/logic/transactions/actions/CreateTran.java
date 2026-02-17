package com.taitl.ex.logic.transactions.actions;

import java.util.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Commit transaction object.
 */
public class CreateTran
{
    TransactionLogic tl;

    public CreateTran(TransactionLogic tl)
    {
        sane(tl, "transactionLogic");
        this.tl = tl;
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
        sane(op, "op", config, "config");
        OpKey.validate(op);
        Tr o = new Tr(op, generateId());
        for (Context context : config.contexts())
        {
            o.addTransaction(forContext(context));
        }
        if (custom != null)
        {
            o.addTransaction(custom);
        }
        return o;
    }

    public Tr forContexts(String op, List<Context> contexts, Transaction custom)
    {
        sane(op, "op", contexts, "contexts");
        OpKey.validate(op);
        Tr o = new Tr(op, generateId());
        for (Context context : contexts)
        {
            o.addTransaction(forContext(context));
        }
        if (custom != null)
        {
            o.addTransaction(custom);
        }
        return o;
    }

    protected UUID generateId()
    {
        return UUID.randomUUID();
    }

    protected static Transaction forContext(Context context)
    {
        Transaction t = context.transactionFactory().get();
        t.op(context.name());
        t.name(context.name());
        t.context(context);
        return t;
    }
}
