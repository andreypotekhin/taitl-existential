package com.taitl.ex.logic.execution.rules;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.transactions.*;

public class RequiresReportTimeEvaluation
{
    public boolean in(ConfigurationIndexes indexes)
    {
        return indexes.hasEventHandlers() || indexes.hasIntents();
    }

    public boolean in(Tr tr, StageName stageName)
    {
        return tr.hasPreparedHandlers(stageName) || tr.hasIntents(stageName);
    }
}
