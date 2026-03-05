package com.taitl.ex.logic.stages.begin;

import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class BeginLogic
{
    protected TransactionLogic tl;

    public BeginLogic(TransactionLogic tl)
    {
        sane(tl, "tl");
        this.tl = tl;
    }

    public <T> void onEvent(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        if (!tr.beginActive() || !tr.shouldEvaluateBegin(runtimeKey))
        {
            return;
        }
        evaluationLogic().evaluateIntents(runtimeKey, tr, StageName.BEGIN);
        evaluationLogic().evaluateBegin(runtimeKey, tr);
    }

    protected EvaluationLogic evaluationLogic()
    {
        return tl.evaluationLogic;
    }
}
