package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * RollbackTr transaction.
 */
public class RollbackTransaction
{
    TransactionLogic tl;

    public RollbackTransaction(TransactionLogic tl)
    {
        sane(tl, "transactionLogic");
        this.tl = tl;
    }

    public void call(Tr tr) throws ExistentialException
    {
        tr.onRollback();
    }
}
