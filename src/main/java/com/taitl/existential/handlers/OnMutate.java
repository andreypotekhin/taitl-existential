package com.taitl.existential.handlers;

import java.util.function.*;
import com.taitl.ex.common.helper.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.types.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.existential.constants.Strings.*;

public class OnMutate<T> implements BiEventHandlerWithSideEffects<T>
{
    Predicate<? super T> condition;
    BiPredicate<? super T, ? super T> bicondition;
    BiConsumer<? super T, ? super T> action;
    String description = null;

    public OnMutate(BiConsumer<? super T, ? super T> action)
    {
        sane(action, "action");
        this.action = action;
    }

    public OnMutate(BiConsumer<? super T, ? super T> action, String description)
    {
        sane(action, "action");
        this.action = action;
        this.description = description;
    }

    public OnMutate(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        this.condition = condition;
        this.action = action;
    }

    public OnMutate(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        this.condition = condition;
        this.action = action;
        this.description = description;
    }

    public OnMutate(BiPredicate<? super T, ? super T> bicondition, BiConsumer<? super T, ? super T> action)
    {
        sane(bicondition, "bicondition", action, "action");
        this.bicondition = bicondition;
        this.action = action;
    }

    public OnMutate(BiPredicate<? super T, ? super T> bicondition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(bicondition, "bicondition", action, "action", description, "description");
        this.bicondition = bicondition;
        this.action = action;
        this.description = description;
    }

    // public OnMutate(Predicate<? super T> condition)
    // {
    // Args.cool(condition, "condition");
    // this.condition = condition;
    // }

    public void handle(T t0, T t1) throws ExistentialException
    {
        sane(t0, "t0", t1, "t1");

        if (action == null)
        {
            State.cool(condition, "condition");

            // This is an event handler without side effects.
            // Check the condition and throw an exception if it is not met.
            if (!condition.test(t1))
            {
                throw new EventHandlerExecutionException(CONDITION_NOT_MET);
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
                throw new EventHandlerExecutionException(e);
            }
        }
    }

    public String description()
    {
        return description == null ? "" : description;
    }
}
