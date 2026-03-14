package com.taitl.ex.logic.stages.checkpoint;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class CheckpointLogic
{
    @Up
    protected TransactionLogic tl;

    public CheckpointLogic(TransactionLogic tl)
    {
        sane(tl, "tl");
        this.tl = tl;
    }

    public void onCheckpoint(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        evaluationLogic().evaluateCheckpoint(tr);
    }

    protected EvaluationLogic evaluationLogic()
    {
        return tl.evaluationLogic;
    }
}
