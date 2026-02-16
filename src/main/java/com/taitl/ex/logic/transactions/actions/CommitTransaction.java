package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Commit transaction
 */
public class CommitTransaction
{
    TransactionLogic tl;

    public CommitTransaction(TransactionLogic tl)
    {
        sane(tl, "transactionLogic");
        this.tl = tl;
    }

    public void call(Tr tr) throws ExistentialException
    {
        tr.onCommit();
        tl.validationLogic.run(tr);
    }
}
