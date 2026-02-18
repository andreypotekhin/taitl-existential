package com.taitl.ex.common.helper;

import java.util.*;
import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Shared helpers for building existential transactions.
 */
public class TransactionCreation
{
    /**
     * Protected constructor for an utility class.
     */
    protected TransactionCreation()
    {
    }

    public static Tr forConfig(String op, Config config, Transaction custom)
    {
        return forConfig(op, config, custom, TransactionCreation::generateId, TransactionCreation::forContext);
    }

    public static Tr forContexts(String op, List<Context> contexts, Transaction custom)
    {
        return forContexts(op, contexts, custom, TransactionCreation::generateId, TransactionCreation::forContext);
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
        t.op(context.name());
        t.name(context.name());
        t.context(context);
        return t;
    }
}
