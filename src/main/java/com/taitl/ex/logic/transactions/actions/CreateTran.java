package com.taitl.ex.logic.transactions.actions;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.logic.configuration.rules.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

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
        return forConfig(op, config, custom, CreateTran::generateId, CreateTran::forContext);
    }

    public Tr forContexts(String op, List<Context> contexts, Transaction custom)
    {
        return forContexts(op, contexts, custom, CreateTran::generateId, CreateTran::forContext);
    }

    public static Tr forConfig(
            String op,
            Config config,
            Transaction custom,
            Supplier<UUID> idSupplier,
            Function<Context, Transaction> contextFactory)
    {
        sane(op, "op", config, "config", idSupplier, "idSupplier", contextFactory, "contextFactory");
        OpKey.validate(op);
        Tr o = new Tr(op, idSupplier.get());
        for (Context context : config.contexts())
        {
            o.addTransaction(contextFactory.apply(context));
        }
        if (custom != null)
        {
            o.addTransaction(custom);
        }
        return o;
    }

    public static Tr forContexts(
            String op,
            List<Context> contexts,
            Transaction custom,
            Supplier<UUID> idSupplier,
            Function<Context, Transaction> contextFactory)
    {
        sane(op, "op", contexts, "contexts", idSupplier, "idSupplier", contextFactory, "contextFactory");
        OpKey.validate(op);
        Tr o = new Tr(op, idSupplier.get());
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

    public static UUID generateId()
    {
        return UUID.randomUUID();
    }

    public static Transaction forContext(Context context)
    {
        Transaction t = context.transactionFactory().get();
        requireTransactionOpMatchesContext(t, context);
        t.op(context.name());
        t.name(context.name());
        t.context(context);
        return t;
    }

    static void requireTransactionOpMatchesContext(Transaction transaction, Context context)
    {
        sane(transaction, "transaction", context, "context");
        if (isDefaultPlaceholderTransaction(transaction))
        {
            return;
        }
        MatchParentName.require(transaction.op, context.name(), "parent context");
    }

    static boolean isDefaultPlaceholderTransaction(Transaction transaction)
    {
        return "undefined".equals(transaction.op)
                && "undefined".equals(transaction.name);
    }
}
