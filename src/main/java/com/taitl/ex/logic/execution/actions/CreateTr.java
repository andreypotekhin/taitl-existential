package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.core.execution.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class CreateTr
{
    TransactionLogic transactionLogic;

    public CreateTr(TransactionLogic transactionLogic)
    {
        sane(transactionLogic, "execution");
        this.transactionLogic = transactionLogic;
    }

    public Tr call(String op, Transaction custom) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        transactionLogic.ex().ops().finalise();
        return transactionLogic.registry().create(op, custom);
    }
}
