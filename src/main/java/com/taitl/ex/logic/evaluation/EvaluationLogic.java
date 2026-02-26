package com.taitl.ex.logic.evaluation;

import com.taitl.ex.core.existential.*;

import java.io.*;

public class EvaluationLogic implements Closeable
{
    protected ExistentialConfigs ec;

    public EvaluationLogic(ExistentialConfigs ec)
    {
        this.ec = ec;
    }

    public void close()
    {
    }
}
