package com.taitl.ex.logic.validation;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.ex.logic.validation.actions.*;
import com.taitl.ex.logic.validation.output.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

/**
 * Run event handlers and evaluate validation expressions
 * on transaction commit() or checkpoint().
 */
public class ValidationLogic
{
    TransactionLogic tl;
    EvaluationLogic el;
    ValidationReport report;
    PrepareTransaction prepareTransaction;
    ValidateTransaction validateTransaction;

    public ValidationLogic(TransactionLogic transactionLogic)
    {
        this.tl = transactionLogic;
        this.el = transactionLogic.evaluationLogic;
        prepareTransaction = Creator.singleton(PrepareTransaction.class);
        validateTransaction = Creator.singleton(ValidateTransaction.class);
        this.report = Creator.create(ValidationReport.class);
    }

    /**
     * Prepare Tr object for validation.
     */
    public void prepareForValidation(Tr tr)
    {
        prepareTransaction.call(tr, this);
    }

    /**
     * Evaluate validation expressions and call event handlers.
     */
    public void run(Tr tr) throws ExistentialException
    {
        if (el == null)
        {
            el = tl.evaluationLogic;
        }
        validateTransaction.call(tr, el, report);
    }
}
