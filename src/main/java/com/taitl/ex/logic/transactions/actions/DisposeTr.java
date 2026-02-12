package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Dispose transaction object after a commit/rollback.
 */
public class DisposeTr
{
    TransactionLogic transactionLogic;

    public DisposeTr(TransactionLogic transactionLogic)
    {
        sane(transactionLogic, "transactionLogic");
        this.transactionLogic = transactionLogic;
    }

    /**
     * Close transaction, remove from registry
     */
    public void call(Tr tr) throws NotFoundException
    {
        tr.close();
        transactionLogic.registry().remove(tr.id.toString());
    }
}
