package com.taitl.ex.logic.configuration.actions;

import java.util.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class CreateTransaction
{
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
