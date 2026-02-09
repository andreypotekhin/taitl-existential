package com.taitl.ex.logic.configuration.actions;

import com.taitl.existential.contexts.*;
import com.taitl.existential.transactions.*;

public class CreateTransaction
{
    /**
     * Create instances of custom transactions for a Context.
     * This method is called by TransactionRegistry.create().
     * @return List of Transaction objects
     */
    public static Transaction call(Context context)
    {
        Transaction tr = context.transactionFactory().get();
        tr.op(context.name());
        tr.name(context.name());
        tr.context(context);
        return tr;
    }
}
