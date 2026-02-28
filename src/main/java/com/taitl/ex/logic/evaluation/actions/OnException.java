package com.taitl.ex.logic.evaluation.actions;

import com.taitl.ex.logic.validation.output.*;
import com.taitl.existential.exceptions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class OnException
{
    public void call(ExistentialException ex, ValidationReport report) throws ExistentialException
    {
        sane(ex, "ex", report, "report");
        if (constraintViolation(ex))
        {
            report.addException(ex);
            return;
        }
        throw ex;
    }

    public static boolean constraintViolation(ExistentialException ex)
    {
        sane(ex, "ex");
        if (ex instanceof ConditionNotMetException
                || ex instanceof InvariantViolation
                || ex instanceof PredicateFailure)
        {
            return true;
        }
        if (!(ex instanceof EventHandlerException))
        {
            return false;
        }
        if (ex.getCause() != null)
        {
            return false;
        }
        String message = ex.getMessage();
        if (message == null)
        {
            return false;
        }
        String normalized = message.toLowerCase(Locale.ROOT);
        return normalized.contains("condition is not met") || normalized.contains("condition not met");
    }
}
