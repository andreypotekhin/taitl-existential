package com.taitl.ex.logic.execution.rules;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.transactions.*;

/**
 * Decides whether entity reporting must visit a BEGIN or IMMEDIATE stage.
 * Both configured handlers and intents count as work; the transaction overload checks the
 * corresponding local additions used when binding an execution plan.
 *
 * This rule concerns entity evaluation at report time. Lifecycle callbacks and later
 * transaction boundaries retain their own execution paths.
 */
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
