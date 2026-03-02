package com.taitl.ex.logic.stages.preconditions;

import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class PreconditionLogic
{
    protected TransactionLogic tl;

    public PreconditionLogic(TransactionLogic tl)
    {
        sane(tl, "tl");
        this.tl = tl;
    }

    public <T> void onEvent(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        if (!tr.preconditionActive() || !tr.shouldEvaluatePrecondition(runtimeKey))
        {
            return;
        }
        evaluationLogic().evaluateIntent(runtimeKey, tr, StageName.PRECONDITION);
        evaluationLogic().evaluatePrecondition(runtimeKey, tr);
    }

    protected EvaluationLogic evaluationLogic()
    {
        return tl.evaluationLogic;
    }
}
