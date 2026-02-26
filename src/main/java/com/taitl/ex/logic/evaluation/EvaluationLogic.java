package com.taitl.ex.logic.evaluation;

import com.taitl.ex.logic.transactions.*;
import com.taitl.ex.logic.validation.output.*;
import com.taitl.existential.transactions.*;

import java.io.*;

public class EvaluationLogic implements Closeable
{
    protected TransactionLogic tl;

    public EvaluationLogic(TransactionLogic tl)
    {
        this.tl = tl;
    }

    /**
     * Evaluate validation expressions and call event handlers.
     * Write any violations to ValidationReport.
     */
    public void evaluate(Tr tr, ValidationReport report)
    {
        // TODO
    }

    public void close()
    {
    }
}
