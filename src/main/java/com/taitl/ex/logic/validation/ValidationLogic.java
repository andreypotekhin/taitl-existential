package com.taitl.ex.logic.validation;

import com.taitl.ex.logic.transactions.*;
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
    ValidationReport report;

    public ValidationLogic(TransactionLogic transactionLogic)
    {
        this.tl = transactionLogic;
        this.report = new ValidationReport();
    }

    /**
     * Evaluate validation expressions and call event handlers
     */
    public void run(Tr tr) throws ValidationStageExceptions
    {
        // TODO: Implement validation logic
        // Evaluate validation expressions and call event handlers
        // Output any violations to ValidationReport
        // In case of violations, raise exception and supply validation report
        if (!report.isEmpty())
        {
            throw new ValidationStageExceptions(
                    report.exceptions().toArray(new ExistentialException[] {}));
        }
    }
}
