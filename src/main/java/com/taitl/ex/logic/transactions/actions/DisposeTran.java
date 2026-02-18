package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.common.helper.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

/**
 * Dispose transaction object after a commit/rollback.
 */
public class DisposeTran extends TransactionActionSupport
{
    public DisposeTran(TransactionLogic tl)
    {
        super(tl);
    }

    /**
     * Close transaction, remove from registry
     */
    public void call(Tr tr) throws NotFoundException
    {
        tr.close();
        tl.registry().remove(tr.id.toString());
    }
}
