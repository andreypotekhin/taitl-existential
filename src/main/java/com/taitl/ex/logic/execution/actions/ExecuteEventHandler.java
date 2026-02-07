package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.common.helper.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;

public class ExecuteEventHandler
{
    public static <T> void handle(On<T> handler, T t) throws ExistentialException
    {
        // TODO: Replace with TRUTH predicate and disallow null action
        if (handler.immutable())
        {
            State.cool(handler.condition, "condition");

            if (!handler.condition.test(t))
            {
                throw new ConditionNotMetException(handler.description());
            }

            return;
        }

        if (handler.condition == null || handler.condition.test(t))
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
}
