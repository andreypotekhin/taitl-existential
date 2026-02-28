package com.taitl.ex.logic.evaluation.intents.actions;

import com.taitl.ex.logic.evaluation.actions.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class EvaluateIntentHandlers
{
    public boolean allowed(List<EventHandler<?>> intents, Event<?> event) throws ExistentialException
    {
        sane(intents, "intents", event, "event");
        for (EventHandler<?> intent : intents)
        {
            if (allows(intent, event))
            {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected boolean allows(EventHandler<?> intent, Event<?> event) throws ExistentialException
    {
        sane(intent, "intent", event, "event");
        try
        {
            if (intent instanceof On<?>)
            {
                ExecuteHandler.handle((On) intent, entity(event));
                return true;
            }

            if (intent instanceof BiEventHandlerWithSideEffects<?>)
            {
                if (!(event instanceof BiEvent<?>))
                {
                    return false;
                }
                BiEvent<?> biEvent = (BiEvent<?>) event;
                ((BiEventHandlerWithSideEffects) intent).handle(biEvent.t0, biEvent.t1);
                return true;
            }

            throw new IllegalStateException("Unsupported intent handler type: " + intent.getClass().getName());
        }
        catch (ExistentialException ex)
        {
            if (intentConditionNotMet(ex))
            {
                return false;
            }
            throw ex;
        }
        catch (RuntimeException ex)
        {
            throw new IntentViolation("Intent evaluation failed. See /Troubleshooting.md#intent-violation", ex);
        }
    }

    protected boolean intentConditionNotMet(ExistentialException ex)
    {
        sane(ex, "ex");
        if (ex instanceof ConditionNotMetException)
        {
            return true;
        }
        if (!(ex instanceof EventHandlerException))
        {
            return false;
        }
        if (ex.getCause() != null)
        {
            return false;
        }
        String message = ex.getMessage();
        if (message == null)
        {
            return false;
        }
        String normalized = message.toLowerCase(Locale.ROOT);
        return normalized.contains("condition is not met") || normalized.contains("condition not met");
    }

    protected Object entity(Event<?> event)
    {
        sane(event, "event");
        if (event instanceof BiEvent<?>)
        {
            BiEvent<?> biEvent = (BiEvent<?>) event;
            return (biEvent.t1 != null) ? biEvent.t1 : biEvent.t0;
        }
        if (event instanceof EntityEvent<?>)
        {
            return ((EntityEvent<?>) event).t;
        }
        throw new IllegalStateException("Unable to derive entity from event type: " + event.getClass().getName());
    }
}
