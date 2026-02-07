package com.taitl.ex.logic.execution;

import com.taitl.ex.domain.execution.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.helper.*;
import com.taitl.existential.keys.*;

public class CreateOpRun
{
    TransactionLogic transactionLogic;

    public CreateOpRun(TransactionLogic transactionLogic)
    {
        Args.cool(transactionLogic, "execution");
        this.transactionLogic = transactionLogic;
    }

    public OpRun call(String op) throws ExistentialException
    {
        Args.cool(op, "op");
        OpKey.validate(op);
        transactionLogic.ex().ops().finalise();
        return transactionLogic.registry().create(op);
    }
}
