package com.taitl.ex.logic.stages.validation.actions;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.actions.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.stages.validation.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Prepares transaction-local handler overlays for event-driven stages.
 */
public class PrepareTransaction
{
    public void call(Tr tr, EvaluationLogic evaluationLogic)
    {
        sane(tr, "tr", evaluationLogic, "evaluationLogic");
        for (StageName stageName : StageName.values())
        {
            prepareStage(tr, evaluationLogic, stageName);
        }
    }

    protected void prepareStage(Tr tr, EvaluationLogic evaluationLogic, StageName stageName)
    {
        sane(tr, "tr", evaluationLogic, "evaluationLogic", stageName, "stageName");
        ConfigurationIndexes indexes = null;
        IndexEvs indexEvs = null;
        for (Transaction transaction : tr.transactions())
        {
            for (Evs<?> evs : transaction.stage().at(stageName))
            {
                if (!(evs instanceof Invariant<?> || evs instanceof Effect<?>))
                {
                    continue;
                }
                if (indexes == null)
                {
                    indexes = createIndexes(evaluationLogic);
                    indexEvs = Creator.create(IndexEvs.class, new Class[] { ConfigurationIndexes.class }, indexes);
                }
                indexEvs.call(evs);
            }
        }
        if (indexes == null)
        {
            return;
        }

        indexes.doneIndexing();
        tr.preparedIndexes(stageName, indexes);
    }

    protected ConfigurationIndexes createIndexes(EvaluationLogic evaluationLogic)
    {
        sane(evaluationLogic, "evaluationLogic");
        ConfigurationIndexes indexes = Creator.create(ConfigurationIndexes.class);
        indexes.useFullClassNames(evaluationLogic.useFullClassNames());
        return indexes;
    }
}
