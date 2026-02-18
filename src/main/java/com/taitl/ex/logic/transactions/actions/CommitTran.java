package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.common.helper.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

/**
 * Commit transaction
 */
public class CommitTran extends TransactionActionSupport
{
    public CommitTran(TransactionLogic tl)
    {
        super(tl);
    }

    public void call(Tr tr) throws ExistentialException
    {
        tr.onCommit();
        tl.validationLogic.run(tr);
    }
}
