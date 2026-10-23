package com.taitl.ex.logic.execution.rules;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.transactions.*;

/**
 * Decides whether a transaction boundary needs to traverse its recorded entities.
 * Shared handlers or intents and transaction-local additions independently require traversal.
 * An empty stage can skip the entity loop even when the transaction contains many records.
 *
 * Lifecycle callbacks run outside that loop and are not suppressed by this decision.
 * The rule only avoids unnecessary traversal; it does not alter rule timing or callback order.
 */
public class RequiresStageTraversal
{
    public boolean in(ConfigurationIndexes indexes, Tr tr, StageName stage)
    {
        return indexes.hasEventHandlers() || indexes.hasIntents() || tr.hasPreparedHandlers(stage)
                || tr.hasIntents(stage);
    }
}
