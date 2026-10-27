package com.taitl.ex.logic.evaluation.events.actions;

import com.taitl.ex.logic.diagnostics.actions.*;

import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;

import static com.taitl.ex.common.helper.Predicates.*;
import static com.taitl.ex.common.helper.State.*;

public class ExecuteHandler
{
    public static <T> void handle(On<T> handler, T t)
    {
        prepare(handler);

        if (handler.immutable())
        {
            validateImmutable(handler, t);
            return;
        }

        if (shouldExecute(handler, t))
        {
            execute(handler, t);
        }
    }

    /**
     * Validates an immutable handler by enforcing its condition.
     */
    private static <T> void validateImmutable(On<T> handler, T t)
    {
        cool(handler.condition, "condition");

        if (!handler.condition.test(t))
        {
            throw NewEvaluationFailure.condition(handler.description());
        }
    }

    /**
     * Determines whether the handler should execute for the given input.
     */
    private static <T> boolean shouldExecute(On<T> handler, T t)
    {
        return handler.condition.test(t);
    }

    private static <T> void prepare(On<T> handler)
    {
        if (handler.condition == null)
        {
            handler.condition = truth();
        }
    }

    /**
     * Executes the handler action while translating exceptions into library errors.
     */
    private static <T> void execute(On<T> handler, T t)
    {
        try
        {
            handler.action.accept(t);
        }
        catch (Exception e)
        {
            throw NewEvaluationFailure.handler("Event handler execution failed", e);
        }
    }
}
