package com.taitl.ex.logic.stages.validation.data;

import com.taitl.ex.logic.indexing.data.*;
import com.taitl.existential.transactions.*;

/**
 * Intermediary data, such as event and handler indexes,
 * to use in validation stage.
 * An instance of this class is owned by Tr class.
 *
 * @see Tr
 */
public class ValidationData
{
    Tr tr;
    public IndexData indexData;

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
