package com.taitl.existential.handlers;

import static com.taitl.existential.constants.Strings.ARG_CONDITION;
import static com.taitl.existential.constants.Strings.ARG_PREDICATE;
import static com.taitl.existential.constants.Strings.ARG_T0_T1;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.exceptions.EventHandlerFailureException;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.handler.types.BiEventHandlerWithSideEffects;
import com.taitl.existential.helper.Args;

public class OnTransit<T> implements BiEventHandlerWithSideEffects<T>
{
    Predicate<? super T> condition;
    BiPredicate<? super T, ? super T> bicondition;
    BiConsumer<? super T, ? super T> action;
    String description = null;

    public OnTransit(BiConsumer<? super T, ? super T> action)
    {
        Args.cool(action, "action");
        this.action = action;
    }

    public OnTransit(BiConsumer<? super T, ? super T> action, String description)
    {
        Args.cool(action, "action");
        this.action = action;
        this.description = description;
    }

    public OnTransit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        this.condition = condition;
        this.action = action;
    }

    public OnTransit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action, String description)
    {
        Args.cool(condition, "condition", action, "action", description, "description");
        this.condition = condition;
        this.action = action;
        this.description = description;
    }

    public OnTransit(BiPredicate<? super T, ? super T> bicondition, BiConsumer<? super T, ? super T> action)
    {
        Args.cool(bicondition, "bicondition", action, "action");
        this.bicondition = bicondition;
        this.action = action;
    }

    public OnTransit(BiPredicate<? super T, ? super T> bicondition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        Args.cool(bicondition, "bicondition", action, "action", description, "description");
        this.bicondition = bicondition;
        this.action = action;
        this.description = description;
    }

    public void handle(T t0, T t1) throws ExistentialException
    {
        if (t0 == null && t1 == null)
        {
            throw new IllegalArgumentException(ARG_T0_T1);
        }

        boolean conditionMet = false;

        if (bicondition != null)
        {
            conditionMet = bicondition.test(t0, t1);
        }
        else if (condition != null)
        {
            conditionMet = condition.test(t1);
        }

        if (conditionMet)
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

    public String description()
    {
        return description == null ? "" : description;
    }
}
