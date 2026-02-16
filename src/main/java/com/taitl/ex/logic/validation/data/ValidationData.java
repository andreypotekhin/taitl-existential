package com.taitl.ex.logic.validation.data;

import com.taitl.ex.logic.indexing.data.runtime_indexes.*;
import com.taitl.existential.transactions.*;

/**
 * Intermediary data, such as event and handler indexes,
 * to use in validation stage.
 * The data in this class is scoped to a single transaction.
 * An instance of ValidationData is owned by Tr class (Tr.indexData).
 * Used by ValidationLogic.
 *
 * @see Tr
 */
public class ValidationData
{
    Tr tr;
    public RuntimeIndexes indexData;

    public ValidationData(Tr tr)
    {
        this.tr = tr;
        this.indexData = tr.runtimeIndexes();
    }

    public void close()
    {
        tr = null;
    }
}
