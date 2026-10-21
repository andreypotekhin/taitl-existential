package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.transactions.*;

/**
 * Dispose transaction object after a commit/rollback.
 */
public class DisposeTran extends TranAction
{
    public DisposeTran(TransactionLogic tl)
    {
        super(tl);
    }

    /**
     * Close transaction, remove from registry
     */
    public void call(Tr tr)
    {
        tl.registry().removeIfPresent(tr.id.toString());
        tr.close();
    }
}
