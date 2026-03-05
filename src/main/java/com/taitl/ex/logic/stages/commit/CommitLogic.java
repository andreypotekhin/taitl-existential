package com.taitl.ex.logic.stages.commit;

import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class CommitLogic
{
    protected TransactionLogic tl;

    public CommitLogic(TransactionLogic tl)
    {
        sane(tl, "tl");
        this.tl = tl;
    }

    public void onCommit(Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        evaluationLogic().evaluateCommit(tr);
    }

    protected EvaluationLogic evaluationLogic()
    {
        return tl.evaluationLogic;
    }
}
