package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.common.helper.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

/**
 * Checkpoint transaction
 */
public class CheckpointTran extends TransactionActionSupport
{
    public CheckpointTran(TransactionLogic tl)
    {
        super(tl);
    }

    public void call(Tr tr) throws ExistentialException
    {
        tr.onCheckpoint();
        tl.validationLogic.run(tr);
    }
}
