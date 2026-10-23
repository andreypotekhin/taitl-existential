package com.taitl.ex.logic.transactions.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.execution.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

/**
 * Begin transaction
 */
public class BeginTran extends TranAction
{
    @Logic
    protected ExecutionPlanLogic executionPlanLogic = Creator.create(ExecutionPlanLogic.class);

    public BeginTran(TransactionLogic tl)
    {
        super(tl);
    }

    public void call(Tr tr) throws ExistentialException
    {
        tr.onBegin();
        tl.validationLogic.prepareForValidation(tr);
        executionPlanLogic.bind(tr, tl.ex().configs().executionPlan(tr.op));
    }
}
