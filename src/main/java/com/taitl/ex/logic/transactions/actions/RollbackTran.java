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

    public void call(Tr tr)
    {
        Throwable failure = null;
        try
        {
            tr.onRollback();
        }
        catch (Throwable caught)
        {
            failure = caught;
        }
        try
        {
            tl.rollbackLogic.onRollback(tr);
        }
        catch (Throwable caught)
        {
            if (failure == null)
            {
                failure = caught;
            }
            else if (failure != caught)
            {
                failure.addSuppressed(caught);
            }
        }
        if (failure instanceof ExistentialException)
        {
            throw (ExistentialException) failure;
        }
        if (failure instanceof RuntimeException)
        {
            throw (RuntimeException) failure;
        }
        if (failure instanceof Error)
        {
            throw (Error) failure;
        }
        if (failure != null)
        {
            throw new ExistentialException("Rollback callback failed", failure);
        }
    }
}
