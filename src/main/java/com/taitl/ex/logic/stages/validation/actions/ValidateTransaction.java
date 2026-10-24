package com.taitl.ex.logic.stages.validation.actions;

import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.stages.validation.output.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

/**
 * Evaluates validation expressions and call event handlers.
 */
public class ValidateTransaction
{
    public void call(Tr tr, EvaluationLogic el, ValidationReport report)
    {
        el.evaluateValidation(tr, report);
        if (!report.isEmpty())
        {
            throw new ValidationStageExceptions(
                    "Validation stage failed for operation '" + tr.op + "' with " + report.exceptions().size()
                            + " error(s). See /Troubleshooting.md#validation-failure",
                    report.exceptions().toArray(new ExistentialException[] {}));
        }
    }
}
