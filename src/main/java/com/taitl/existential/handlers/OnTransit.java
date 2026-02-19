package com.taitl.existential.handlers;

import java.util.function.*;
import com.taitl.existential.events.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.types.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.existential.constants.Strings.*;

/**
 * Declarative handler for {@link Transit} events that involve two values.
 * The handler can be guarded by a predicate on the new value or a bi-predicate
 * on both values. When the condition passes, the action is invoked and any
 * execution failures are wrapped as {@link EventHandlerExecutionException}.
 *
 * @param <T>
 *            Type of entity transitioning between values
 */
public class OnTransit<T> implements BiEventHandlerWithSideEffects<T>
{
    Predicate<? super T> condition;
    BiPredicate<? super T, ? super T> bicondition;
    BiConsumer<? super T, ? super T> action;
    String description = null;

    public OnTransit(BiConsumer<? super T, ? super T> action)
    {
        sane(action, "action");
        this.action = action;
    }

    public OnTransit(BiConsumer<? super T, ? super T> action, String description)
    {
        sane(action, "action");
        this.action = action;
        this.description = description;
    }

    public OnTransit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action)
    {
        sane(condition, "condition", action, "action");
        this.condition = condition;
        this.action = action;
    }

    public OnTransit(Predicate<? super T> condition, BiConsumer<? super T, ? super T> action, String description)
    {
        sane(condition, "condition", action, "action", description, "description");
        this.condition = condition;
        this.action = action;
        this.description = description;
    }

    public OnTransit(BiPredicate<? super T, ? super T> bicondition, BiConsumer<? super T, ? super T> action)
    {
        sane(bicondition, "bicondition", action, "action");
        this.bicondition = bicondition;
        this.action = action;
    }

    public OnTransit(BiPredicate<? super T, ? super T> bicondition, BiConsumer<? super T, ? super T> action,
            String description)
    {
        sane(bicondition, "bicondition", action, "action", description, "description");
        this.bicondition = bicondition;
        this.action = action;
        this.description = description;
    }

    /**
     * Handles a transition between two values.
     *
     * @param t0
     *            Previous value (may be null)
     * @param t1
     *            New value (may be null)
     * @throws ExistentialException
     *            If validation fails or action execution errors
     */
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
                throw new EventHandlerExecutionException(e);
            }
        }
    }

    /**
     * Returns the handler description or an empty string if absent.
     *
     * @return human-friendly description for diagnostics/logging
     */
    public String description()
    {
        return description == null ? "" : description;
    }
}
