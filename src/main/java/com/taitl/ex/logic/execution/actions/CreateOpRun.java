package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.common.helper.*;
import com.taitl.ex.core.execution.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

public class CreateOpRun
{
    TransactionLogic transactionLogic;

    public CreateOpRun(TransactionLogic transactionLogic)
    {
        Args.sane(transactionLogic, "execution");
        this.transactionLogic = transactionLogic;
    }

    public OpRun call(String op, Transaction custom) throws ExistentialException
    {
        Args.sane(op, "op");
        OpKey.validate(op);
        transactionLogic.ex().ops().finalise();
        return transactionLogic.registry().create(op, custom);
    }
}
