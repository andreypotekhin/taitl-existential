package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Dispose transaction object after a commit/rollback.
 */
public class DisposeTran
{
    TransactionLogic tl;

    public DisposeTran(TransactionLogic tl)
    {
        sane(tl, "transactionLogic");
        this.tl = tl;
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
