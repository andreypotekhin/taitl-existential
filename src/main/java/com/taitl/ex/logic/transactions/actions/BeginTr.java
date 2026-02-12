package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Begin transaction
 */
public class BeginTr
{
    TransactionLogic transactionLogic;

    public BeginTr(TransactionLogic transactionLogic)
    {
        sane(transactionLogic, "transactionLogic");
        this.transactionLogic = transactionLogic;
    }

    /**
     * Execute transaction predicates
     */
    public void call(Tr tr) throws NotFoundException
    {
        // TODO: Execute transaction predicates
    }
}
