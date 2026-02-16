package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Begin transaction
 */
public class BeginTransaction
{
    TransactionLogic tl;

    public BeginTransaction(TransactionLogic tl)
    {
        sane(tl, "transactionLogic");
        this.tl = tl;
    }

    public void call(Tr tr) throws NotFoundException
    {
        tr.onBegin();
        tl.validationLogic.prepareForValidation(tr);
        // TODO: Execute transaction preconditions
    }
}
