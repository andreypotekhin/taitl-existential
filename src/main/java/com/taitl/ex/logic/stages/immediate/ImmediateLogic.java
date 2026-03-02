package com.taitl.ex.logic.stages.immediate;

import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class ImmediateLogic
{
    protected TransactionLogic tl;

    public ImmediateLogic(TransactionLogic tl)
    {
        sane(tl, "tl");
        this.tl = tl;
    }

    public <T> void onEvent(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        if (!tr.immediateActive())
        {
            return;
        }
        evaluationLogic().evaluateIntent(runtimeKey, tr, StageName.IMMEDIATE);
        evaluationLogic().evaluateImmediate(runtimeKey, tr);
    }

    protected EvaluationLogic evaluationLogic()
    {
        return tl.evaluationLogic;
    }
}
