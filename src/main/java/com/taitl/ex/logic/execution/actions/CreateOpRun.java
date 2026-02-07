package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.common.helper.*;
import com.taitl.ex.domain.execution.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;

public class CreateOpRun
{
    TransactionLogic transactionLogic;

    public CreateOpRun(TransactionLogic transactionLogic)
    {
        Args.sane(transactionLogic, "execution");
        this.transactionLogic = transactionLogic;
    }

    public OpRun call(String op) throws ExistentialException
    {
        Args.sane(op, "op");
        OpKey.validate(op);
        transactionLogic.ex().ops().finalise();
        return transactionLogic.registry().create(op);
    }
}
