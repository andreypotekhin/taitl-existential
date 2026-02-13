package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Commit transaction object.
 */
public class CreateTr
{
    TransactionLogic transactionLogic;

    public CreateTr(TransactionLogic transactionLogic)
    {
        sane(transactionLogic, "transactionLogic");
        this.transactionLogic = transactionLogic;
    }

    public Tr call(String op, Transaction custom) throws ExistentialException
    {
        sane(op, "op");
        OpKey.validate(op);
        verify(transactionLogic.ex().configured(), "You should call configs().done() first");
        return transactionLogic.registry().create(op, custom);
    }
}
