package com.taitl.ex.logic.evaluation;

import com.taitl.ex.logic.transactions.*;

import java.io.*;

public class EvaluationLogic implements Closeable
{
    protected TransactionLogic tl;

    public EvaluationLogic(TransactionLogic tl)
    {
        this.tl = tl;
    }

    public void close()
    {
    }
}
