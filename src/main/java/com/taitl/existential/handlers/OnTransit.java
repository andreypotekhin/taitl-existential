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
 * Event handler for Transit events - events that involve before- and after- states of entity.
 *
 * @param <T>
 *            Type of entity being mutated
 *
 * @see Transit
 */
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
     * Handles entity transition between before- and after- states.
     *
     * @param t0
     *            Previous value
     * @param t1
     *            New value
     * @throws ExistentialException
     *            If validation fails or action execution errors
     */
    public void handle(T t0, T t1) throws ExistentialException
    {
        sane(t0, "t0", t1, "t1");

        if (action == null)
        {
            if (bicondition != null)
            {
                if (!bicondition.test(t0, t1))
                {
                    throw new EventHandlerException(handlerMessage("The specified condition is not met"));
                }
                return;
            }

            State.cool(condition, "condition");

            if (!condition.test(t1))
            {
                throw new EventHandlerException(handlerMessage("The specified condition is not met"));
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
                throw new EventHandlerException(handlerMessage("Event handler execution failed"), e);
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
