package com.taitl.existential.handlers;

import com.taitl.ex.common.helper.*;
import com.taitl.ex.common.helper.strings.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.types.*;

import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Declarative handler for {@link Transit} events that involve two values.
 * The handler can be guarded by a predicate on the new value or a bi-predicate
 * on both values. When the condition passes, the action is invoked and any
 * execution failures are wrapped as {@link EventHandlerException}.
 *
 * @param <T>
 *            Type of entity transitioning between values
 */
// TODO: rename to OnPort
public class OnTransit<T> implements BiEventHandler<T>
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
        sane(condition, "condition", description, "description");
        if (action != null)
        {
            sane(action, "action");
        }
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
        sane(bicondition, "bicondition", description, "description");
        if (action != null)
        {
            sane(action, "action");
        }
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
        PairArgs.requireNotBothNull(t0, t1, "Arguments 't0' and 't1' should not be both null");

        boolean conditionMet = true;

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
            if (action == null)
            {
                return;
            }

            try
            {
                action.accept(t0, t1);
            }
            catch (Exception e)
            {
                throw new EventHandlerException(handlerMessage("Event handler execution failed"), e);
            }
        }
        else if (action == null)
        {
            throw new EventHandlerException(handlerMessage("The specified condition is not met"));
        }
    }

    /**
     * Returns the handler description or an empty string if absent.
     *
     * @return human-friendly description for diagnostics/logging
     */
    public String description()
    {
        return Descriptions.text(description);
    }

    protected String handlerMessage(String base)
    {
        return Descriptions.message(base, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Transit.class);
    }
}
