package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

/**
 * Begin transaction
 */
public class BeginTran extends TranAction
{
    public BeginTran(TransactionLogic tl)
    {
        super(tl);
    }

    public void call(Tr tr) throws NotFoundException
    {
        tr.onBegin();
        tl.validationLogic.prepareForValidation(tr);
        // TODO: Execute transaction preconditions
    }
}
