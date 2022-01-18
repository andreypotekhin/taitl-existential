package com.taitl.existential.event_handlers;

import static com.taitl.existential.constants.Strings.ARG_CONDITION;
import static com.taitl.existential.constants.Strings.ARG_PREDICATE;
import static com.taitl.existential.constants.Strings.ARG_T0_T1;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.taitl.existential.exceptions.EventHandlerFailureException;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.interfaces.MutationHandler;

public class OnPermutate<T> implements MutationHandler<T>
{
    Predicate<? super T> condition;
    BiConsumer<? super T, ? super T> action;

    public OnPermutate(BiConsumer<? super T, ? super T> action)
    {
        if (action == null)
        {
            throw new IllegalArgumentException(ARG_PREDICATE);
        }
        this.action = action;
    }

    public OnPermutate(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        if (condition == null)
        {
            throw new IllegalArgumentException(ARG_CONDITION);
        }
        if (action == null)
        {
            throw new IllegalArgumentException(ARG_PREDICATE);
        }
        this.condition = condition;
        this.action = action;
    }

    public void handle(T t0, T t1) throws ExistentialException
    {
        if (t0 == null && t1 == null)
        {
            throw new IllegalArgumentException(ARG_T0_T1);
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
