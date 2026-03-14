package com.taitl.ex.logic.stages.rollback;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class RollbackLogic
{
    @Up
    protected TransactionLogic tl;

    public RollbackLogic(TransactionLogic tl)
    {
        sane(tl, "tl");
        this.tl = tl;
    }

    public void onRollback(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        evaluationLogic().evaluateRollback(tr);
    }

    protected EvaluationLogic evaluationLogic()
    {
        return tl.evaluationLogic;
    }
}
