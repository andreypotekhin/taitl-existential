package com.taitl.ex.logic.configuration.actions;

import java.util.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class CreateTransaction
{
    public Tr forContexts(String op, List<Context> contexts, Transaction custom)
    {
        sane(op, "op");
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

    /**
     * Create an instance of a Transaction for a Context.
     */
    public static Transaction forContext(Context context)
    {
        Transaction tr = context.transactionFactory().get();
        tr.op(context.name());
        tr.name(context.name());
        tr.context(context);
        return tr;
    }

    protected UUID generateId()
    {
        return UUID.randomUUID();
    }
}
