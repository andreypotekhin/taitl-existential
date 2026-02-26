package com.taitl.ex.logic.evaluation.actions;

import com.taitl.ex.logic.validation.output.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class ExecuteHandlers
{
    protected OnException onException = new OnException();

    public void call(List<Ev<?>> evs, Event<?> event, ValidationReport report)
            throws ExistentialException
    {
        sane(evs, "evs", event, "event", report, "report");
        for (Ev<?> ev : evs)
        {
            if (!(ev instanceof EventHandler<?>))
            {
                continue;
            }
            executeHandler((EventHandler<?>) ev, event, report);
        }
    }

    protected void executeHandler(EventHandler<?> handler, Event<?> event, ValidationReport report)
            throws ExistentialException
    {
        sane(handler, "handler", event, "event", report, "report");
        try
        {
            callEventHandler(handler, event);
        }
        catch (ExistentialException ex)
        {
            routeHandlerException(ex, report);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void callEventHandler(EventHandler<?> handler, Event<?> event)
            throws ExistentialException
    {
        sane(handler, "handler", event, "event");

        if (handler instanceof On<?>)
        {
            ExecuteHandler.handle((On) handler, entity(event));
            return;
        }

        if (handler instanceof BiEventHandlerWithSideEffects<?>)
        {
            if (!(event instanceof BiEvent<?>))
            {
                throw new IllegalStateException("Bi-event handler requires BiEvent runtime event");
            }
            BiEvent<?> biEvent = (BiEvent<?>) event;
            ((BiEventHandlerWithSideEffects) handler).handle(biEvent.t0, biEvent.t1);
            return;
        }

        if (handler instanceof EventHandlerWithSideEffects<?>)
        {
            ((EventHandlerWithSideEffects) handler).handle(entity(event));
            return;
        }

        throw new IllegalStateException("Unsupported event handler type: " + handler.getClass().getName());
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
        throw new IllegalStateException(
                "Unable to derive handler entity from event type: " + event.getClass().getName());
    }

    protected void routeHandlerException(ExistentialException ex, ValidationReport report) throws ExistentialException
    {
        onException.call(ex, report);
    }

    protected boolean constraintViolation(ExistentialException ex)
    {
        return onException.constraintViolation(ex);
    }
}
