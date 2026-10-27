package com.taitl.ex.logic.evaluation.intents.actions;

import com.taitl.ex.logic.evaluation.events.actions.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

import static com.taitl.ex.common.helper.Args.*;

public class EvaluateIntent
{
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean call(EventHandler<?> intent, Event<?> event)
    {
        sane(intent, "intent", event, "event");
        try
        {
            require(intent, event);
            return true;
        }
        catch (ExistentialException ex)
        {
            if (OnException.constraintViolation(ex))
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void require(EventHandler<?> intent, Event<?> event)
    {
        if (intent instanceof On<?>)
        {
            ExecuteHandler.handle((On) intent, entity(event));
            return;
        }

        if (intent instanceof BiEventHandler<?>)
        {
            if (!(event instanceof BiEvent<?>))
            {
                throw new ConditionNotMetException("Intent requires a before/after event");
            }
            BiEvent<?> biEvent = (BiEvent<?>) event;
            ((BiEventHandler) intent).handle(biEvent.t0, biEvent.t1);
            return;
        }

        throw new IllegalStateException("Unsupported intent handler type: " + intent.getClass().getName());
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
