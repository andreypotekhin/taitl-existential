package com.taitl.existential.handlers;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.taitl.existential.exceptions.EventHandlerFailureException;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.handler.types.BiEventHandlerWithSideEffects;
import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;

import static com.taitl.existential.constants.Strings.*;

public class OnMutate<T> implements BiEventHandlerWithSideEffects<T>
{
    Predicate<? super T> condition;
    BiConsumer<? super T, ? super T> action;

    public OnMutate(BiConsumer<? super T, ? super T> action)
    {
        Args.cool(action, "action");
        this.action = action;
    }

    public OnMutate(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        this.condition = condition;
        this.action = action;
    }

    public OnMutate(Predicate<? super T> condition)
    {
        Args.cool(condition, "condition");
        this.condition = condition;
    }

    public void handle(T t0, T t1) throws ExistentialException
    {
        Args.cool(t0, "t0", t1, "t1");

        if (action == null)
        {
            State.cool(condition, "condition");

            // This is an event handler without side effects.
            // Check the condition and throw an exception if it is not met.
            if (!condition.test(t1))
            {
                throw new EventHandlerFailureException(CONDITION_NOT_MET);
            }

            return;
        }

        if (condition == null || condition.test(t1))
        {
            try
            {
                action.accept(t0, t1);
            }
            catch (Exception e)
            {
                throw new EventHandlerFailureException(e);
            }
        }
    }
}
