package com.taitl.ex.logic.stages.validation.actions;

import com.taitl.ex.logic.diagnostics.*;
import com.taitl.ex.common.creator.*;
import com.taitl.existential.constants.*;

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
            throw Creator.singleton(DiagnosticLogic.class)
                    .aggregate(tr.op, StageName.VALIDATION,
                            tr.evaluationBoundary(), report.exceptions(), report.evaluationStopped);
        }
    }
}
