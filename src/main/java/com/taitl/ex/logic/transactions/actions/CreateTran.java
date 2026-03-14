package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.common.creator.Creator;
import com.taitl.ex.logic.configuration.rules.MatchParentName;
import com.taitl.ex.logic.events.EventLogic;
import com.taitl.ex.logic.transactions.TransactionLogic;
import com.taitl.existential.configs.Config;
import com.taitl.existential.configs.Context;
import com.taitl.existential.configs.Transaction;
import com.taitl.existential.keys.OpKey;
import com.taitl.existential.transactions.Tr;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.taitl.ex.common.helper.Args.sane;

/**
 * Commit transaction object.
 */
public class CreateTran extends TranAction
{
    public CreateTran(TransactionLogic tl)
    {
        super(tl);
    }

    public Tr forConfig(String op, Config config, Transaction custom)
    {
        return forConfig(op, config, custom, CreateTran::forContext);
    }

    protected Tr forContexts(String op, List<Context> contexts, Transaction custom)
    {
        return forContexts(op, contexts, custom, CreateTran::forContext);
    }

    protected Tr forConfig(
            String op,
            Config config,
            Transaction custom,
            Function<Context, Transaction> contextFactory)
    {
        sane(op, "op", config, "config", contextFactory, "contextFactory");
        OpKey.validate(op);
        // Tr o = new Tr(op, generateId(), tl, tl.ex().events().eventLogic);
        Tr o = trInstance(op);
        for (Context context : config.contexts())
        {
            if (!MatchParentName.matches(op, context.name()))
            {
                continue;
            }
            o.addTransaction(contextFactory.apply(context));
        }
        if (custom != null)
        {
            o.addTransaction(custom);
        }
        return o;
    }

    protected Tr forContexts(
            String op,
            List<Context> contexts,
            Transaction custom,
            Function<Context, Transaction> contextFactory)
    {
        sane(op, "op", contexts, "contexts", contextFactory, "contextFactory");
        OpKey.validate(op);
        // Tr o = new Tr(op, generateId(), tl, tl.ex().events().eventLogic);
        Tr o = trInstance(op);
        for (Context context : contexts)
        {
            o.addTransaction(contextFactory.apply(context));
        }
        if (custom != null)
        {
            o.addTransaction(custom);
        }
        return o;
    }

    protected static UUID generateId()
    {
        return UUID.randomUUID();
    }

    protected static Transaction forContext(Context context)
    {
        Transaction t = context.transactionFactory().apply(context.name(), context.name());
        requireTransactionOpMatchesContext(t, context);
        t.op(context.name());
        t.name(context.name());
        t.context(context);
        return t;
    }

    protected Tr trInstance(String op)
    {
        // return new Tr(op, generateId(), tl, tl.ex().events().eventLogic);
        return Creator.create(Tr.class,
                new Class[] { String.class, UUID.class, TransactionLogic.class, EventLogic.class },
                op, generateId(), tl, tl.ex().events().eventLogic);
    }

    static void requireTransactionOpMatchesContext(Transaction transaction, Context context)
    {
        sane(transaction, "transaction", context, "context");
        MatchParentName.require(transaction.op, context.name(), "parent context");
    }
}
