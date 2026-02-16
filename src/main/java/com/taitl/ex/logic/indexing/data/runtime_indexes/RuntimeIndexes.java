package com.taitl.ex.logic.indexing.data.runtime_indexes;

import com.taitl.ex.logic.validation.data.*;
import com.taitl.existential.transactions.*;

/**
 * Stores the results of indexing process for use in rule evaluations
 * by various processing stages (runtime, validation).
 * Includes runtime indexes to help with efficient rule evaluation.
 * The data in this class is scoped to a single transaction.
 * An instance of RuntimeIndexes is owned by Tr class (Tr.indexes).
 * Used by ValidationData.
 *
 * @see Tr
 * @see ValidationData
 */
public class RuntimeIndexes
{
    EventField eventField;

    public RuntimeIndexes()
    {
        this.eventField = new EventField();
    }

    public void close()
    {
    }
}
