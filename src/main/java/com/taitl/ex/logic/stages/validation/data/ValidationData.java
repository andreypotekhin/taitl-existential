package com.taitl.ex.logic.stages.validation.data;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.concrete.*;
import com.taitl.ex.logic.indexing.data.*;

/**
 * Intermediary data, such as event and handler indexes,
 * to use in validation stage.
 * An instance of this class is owned by ConcreteTr.
 *
 * @see ConcreteTr
 */
public class ValidationData
{
    @Up
    ConcreteTr tr;

    @Up
    public IndexData indexData;

    public ValidationData(ConcreteTr tr)
    {
        this.tr = tr;
        this.indexData = tr.runtimeIndexes();
    }

    public void close()
    {
        tr = null;
    }
}
