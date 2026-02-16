package com.taitl.ex.logic.indexing.data;

import com.taitl.ex.logic.indexing.data.config_indexes.*;
import com.taitl.ex.logic.indexing.data.runtime_indexes.*;
import com.taitl.ex.logic.validation.data.*;
import com.taitl.existential.transactions.*;

/**
 * Stores the results of indexing process for use in rule evaluations
 * by various processing stages (runtime, validation).
 * Includes configuration and runtime indexes to help with efficient rule evaluation.
 * The data in this class is scoped to a single transaction.
 * An instance of IndexData is owned by Tr class (Tr.indexData).
 * Used by ValidationData.
 *
 * @see Tr
 * @see ValidationData
 */
public class IndexData
{
    ConfigIndexes configIndexes;
    RuntimeIndexes runtimeIndexes;

    public IndexData()
    {
        runtimeIndexes = new RuntimeIndexes();
    }

    // public RuntimeIndexes configIndexes(String op)
    // {
    // // Config config = ConfigRegistry.getConfig(op)
    // // return config.configIndexes();
    // }

    public RuntimeIndexes runtimeIndexes(Tr tr)
    {
        return tr.runtimeIndexes();
    }

    public void close()
    {
    }
}
