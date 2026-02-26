package com.taitl.ex.logic.evaluation.actions;

import com.taitl.ex.logic.validation.output.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class ExecuteHandlers
{
    public void call(List<Ev<?>> evs, Set<RuntimeKey<?>> keys, ValidationReport report) throws ExistentialException
    {
        sane(evs, "evs", keys, "splitKeys", report, "report");
        for (Ev<?> ev : evs)
        {
            if (!(ev instanceof EventHandler<?>))
            {
                continue;
            }
            executeHandler((EventHandler<?>) ev, keys, report);
        }
    }

    protected void executeHandler(EventHandler<?> handler, Set<RuntimeKey<?>> keys, ValidationReport report)
            throws ExistentialException
    {
        sane(handler, "handler", keys, "splitKeys", report, "report");
        for (RuntimeKey<?> key : keys)
        {
            if (!matches(handler, key))
            {
                continue;
            }

            try
            {
                callEventHandler(handler, key);
            }
            catch (ExistentialException ex)
            {
                routeHandlerException(ex, report);
            }
            return;
        }
    }

    protected boolean matches(EventHandler<?> handler, RuntimeKey<?> key)
    {
        sane(handler, "handler", key, "runtimeKey");
        Class<?> eventClass = handler.eventType().eventClass();
        Event<?> event = key.event();
        return event != null && eventClass.isInstance(event);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void callEventHandler(EventHandler<?> handler, RuntimeKey<?> key) throws ExistentialException
    {
        sane(handler, "handler", key, "runtimeKey");

        if (handler instanceof On<?>)
        {
            ExecuteHandler.handle((On) handler, key.entity());
            return;
        }

        if (handler instanceof BiEventHandlerWithSideEffects<?>)
        {
            Event<?> event = key.event();
            if (!(event instanceof BiEvent<?>))
            {
                throw new IllegalStateException(
                        "Bi-event handler requires BiEvent runtime event, got: " + event.getClass().getName());
            }
            BiEvent biEvent = (BiEvent) event;
            ((BiEventHandlerWithSideEffects) handler).handle(biEvent.t0, biEvent.t1);
            return;
        }

        if (handler instanceof EventHandlerWithSideEffects<?>)
        {
            ((EventHandlerWithSideEffects) handler).handle(key.entity());
            return;
        }

        throw new IllegalStateException("Unsupported event handler type: " + handler.getClass().getName());
    }

    protected void routeHandlerException(ExistentialException ex, ValidationReport report) throws ExistentialException
    {
        sane(ex, "ex", report, "report");
        if (constraintViolation(ex))
        {
            report.addException(ex);
            return;
        }
        throw ex;
    }

    protected boolean constraintViolation(ExistentialException ex)
    {
        sane(ex, "ex");
        if (ex instanceof ConditionNotMetException || ex instanceof InvariantViolation)
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
}
