package com.taitl.ex.logic.events.actions;

import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;

import static com.taitl.ex.common.helper.State.*;

public class ExecuteHandler
{
    public static <T> void handle(On<T> handler, T t) throws ExistentialException
    {
        // TODO: Replace with TRUTH predicate and disallow null action
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
    private static <T> void validateImmutable(On<T> handler, T t) throws ExistentialException
    {
        cool(handler.condition, "condition");

        if (!handler.condition.test(t))
        {
            throw new ConditionNotMetException(handler.description());
        }
    }

    /**
     * Determines whether the handler should execute for the given input.
     */
    private static <T> boolean shouldExecute(On<T> handler, T t)
    {
        return handler.condition == null || handler.condition.test(t);
    }

    /**
     * Executes the handler action while translating exceptions into library errors.
     */
    private static <T> void execute(On<T> handler, T t) throws ExistentialException
    {
        try
        {
            handler.action.accept(t);
        }
        catch (Exception e)
        {
            throw new EventHandlerExecutionException(e);
        }
    }
}
