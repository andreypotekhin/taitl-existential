package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

/**
 * RollbackTr transaction.
 */
public class RollbackTran extends TranAction
{
    public RollbackTran(TransactionLogic tl)
    {
        super(tl);
    }

    public void call(Tr tr) throws ExistentialException
    {
        tr.onRollback();
    }
}
