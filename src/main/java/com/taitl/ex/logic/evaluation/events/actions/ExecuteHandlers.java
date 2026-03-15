package com.taitl.ex.logic.evaluation.events.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.stages.validation.output.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class ExecuteHandlers
{
    @Logic
    protected OnException onException = Creator.create(OnException.class);

    public <T> void call(List<Ev<T>> evs, Event<T> event, ValidationReport report)
            throws ExistentialException
    {
        sane(evs, "evs", event, "event", report, "report");
        for (Ev<T> ev : evs)
        {
            if (ev instanceof EventHandler<?>)
            {
                executeHandler((EventHandler<T>) ev, event, report);
            }
            else if (ev instanceof Expression<?>)
            {
                executeExpression((Expression<T>) ev, event, report);
            }
            else
            {
                throw new IllegalStateException("Unsupported evaluable type: " + ev.getClass().getName());
            }
        }
    }

    protected <T> void executeHandler(EventHandler<T> handler, Event<T> event, ValidationReport report)
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

    protected <T> void executeExpression(Expression<T> expression, Event<T> event, ValidationReport report)
            throws ExistentialException
    {
        sane(expression, "expression", event, "event", report, "report");
        try
        {
            callExpression(expression, event);
        }
        catch (ExistentialException ex)
        {
            routeHandlerException(ex, report);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> void callEventHandler(EventHandler<T> handler, Event<T> event)
            throws ExistentialException
    {
        sane(handler, "handler", event, "event");

        if (handler instanceof On<?>)
        {
            ExecuteHandler.handle((On) handler, entity(event));
            return;
        }

        if (handler instanceof BiEventHandler<?>)
        {
            callBiHandler((BiEventHandler) handler, event);
            return;
        }

        if (handler instanceof UniEventHandler<?>)
        {
            ((UniEventHandler) handler).handle(entity(event));
            return;
        }

        throw new IllegalStateException("Unsupported event handler type: " + handler.getClass().getName());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void callBiHandler(BiEventHandler handler, Event<?> event) throws ExistentialException
    {
        sane(handler, "handler", event, "event");
        if (event instanceof BiEvent<?>)
        {
            BiEvent<?> biEvent = (BiEvent<?>) event;
            handler.handle(biEvent.t0, biEvent.t1);
            return;
        }
        if (!supportsSyntheticBiPayload(event))
        {
            throw new IllegalStateException("Bi-event handler requires BiEvent runtime event");
        }
        Object entity = entity(event);
        handler.handle(entity, entity);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> void callExpression(Expression<T> expression, Event<T> event) throws ExistentialException
    {
        sane(expression, "expression", event, "event");
        ((Expression) expression).evaluate(entity(event));
    }

    protected <T> Object entity(Event<T> event)
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

    protected boolean supportsSyntheticBiPayload(Event<?> event)
    {
        sane(event, "event");
        return event instanceof Create<?>
                || event instanceof Update<?>
                || event instanceof Delete<?>
                || event instanceof CU<?>
                || event instanceof UD<?>
                || event instanceof CUD<?>;
    }

    protected void routeHandlerException(ExistentialException ex, ValidationReport report) throws ExistentialException
    {
        onException.call(ex, report);
    }
}
