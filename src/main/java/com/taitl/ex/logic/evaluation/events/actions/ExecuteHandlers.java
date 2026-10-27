package com.taitl.ex.logic.evaluation.events.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.logic.diagnostics.*;
import com.taitl.ex.logic.diagnostics.data.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.constants.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.stages.validation.output.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Runs one ordered event selection and routes ordinary constraint failures to its report. When
 * the caller marks effects as participating, the first newly reported ordinary violation stops
 * this selection; unexpected callback failures still escape immediately for every policy.
 */
public class ExecuteHandlers
{
    @Logic
    protected OnException onException = Creator.create(OnException.class);

    @Logic
    protected DiagnosticLogic diagnostics = Creator.singleton(DiagnosticLogic.class);

    public <T> void call(List<Ev<T>> evs, Event<T> event, ValidationReport report)
    {
        call(evs, event, report, false);
    }

    public <T> void call(List<Ev<T>> evs, Event<T> event, ValidationReport report, boolean stopAfterViolation)
    {
        call(evs, event, report, stopAfterViolation, null);
    }

    public <T> void call(List<Ev<T>> evs, Event<T> event, ValidationReport report,
            boolean stopAfterViolation, Tr tr)
    {
        call(evs, event, report, stopAfterViolation, tr, null, null, null);
    }

    public <T> void call(List<Ev<T>> evs, Event<T> event, ValidationReport report,
            boolean stopAfterViolation, Tr tr, RuleSelection selection, RuntimeKey<?> key, StageName stage)
    {
        sane(evs, "evs", event, "event", report, "report");
        if (stopAfterViolation && !report.isEmpty())
        {
            return;
        }
        for (int i = 0; i < evs.size(); i++)
        {
            int failures = report.exceptions().size();
            RuleDeclaration declaration = selection == null ? null : selection.declaration(i);
            execute(evs.get(i), event, report, tr, declaration == null ? null : declaration.origin, key, stage,
                    stopAfterViolation);
            if (shouldStop(stopAfterViolation, failures, report))
            {
                report.evaluationStopped = true;
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> void execute(Ev<T> ev, Event<T> event, ValidationReport report, Tr tr, RuleOrigin origin,
            RuntimeKey<?> key, StageName stage, boolean stopAfterViolation)
    {
        try
        {
            if (ev instanceof EventHandler<?>)
            {
                callEventHandler((EventHandler<T>) ev, event);
            }
            else if (ev instanceof Expression<?>)
            {
                if (tr == null)
                {
                    callExpression((Expression<T>) ev, event);
                }
                else
                {
                    ((Expression<T>) ev).evaluate((T) entity(event), tr);
                }
            }
            else
            {
                throw new IllegalStateException("Unsupported evaluable type: " + ev.getClass().getName());
            }
        }
        catch (RuntimeException failure)
        {
            boolean ordinary = failure instanceof ExistentialException
                    && OnException.constraintViolation((ExistentialException) failure);
            RuntimeException diagnosed = tr == null ? failure
                    : diagnostics.failure(failure, tr.op, origin, key,
                            stage, tr.evaluationBoundary(), false,
                            stage != StageName.ROLLBACK && (!ordinary || stopAfterViolation));
            if (ordinary && diagnosed instanceof ExistentialException)
            {
                routeHandlerException((ExistentialException) diagnosed, report);
            }
            else
            {
                throw diagnosed;
            }
        }
    }

    protected boolean shouldStop(boolean stopAfterViolation, int previousFailures, ValidationReport report)
    {
        return stopAfterViolation && report.exceptions().size() > previousFailures;
    }

    public <T> void callAll(List<Ev<T>> evs, Event<T> event, ValidationReport report)
    {
        callAll(evs, event, report, null);
    }

    public <T> void callAll(List<Ev<T>> evs, Event<T> event, ValidationReport report, Tr tr)
    {
        callAll(evs, event, report, tr, null, null, null);
    }

    public <T> void callAll(List<Ev<T>> evs, Event<T> event, ValidationReport report, Tr tr,
            RuleSelection selection, RuntimeKey<?> key, StageName stage)
    {
        sane(evs, "evs", event, "event", report, "report");
        Throwable failure = null;
        for (int i = 0; i < evs.size(); i++)
        {
            try
            {
                RuleDeclaration declaration = selection == null ? null : selection.declaration(i);
                execute(evs.get(i), event, report, tr, declaration == null ? null : declaration.origin, key, stage,
                        false);
            }
            catch (Throwable caught)
            {
                if (failure == null)
                {
                    failure = caught;
                }
                else if (failure != caught)
                {
                    failure.addSuppressed(caught);
                }
            }
        }
        rethrow(failure);
    }

    protected void rethrow(Throwable failure)
    {
        if (failure instanceof ExistentialException)
        {
            throw (ExistentialException) failure;
        }
        if (failure instanceof RuntimeException)
        {
            throw (RuntimeException) failure;
        }
        if (failure instanceof Error)
        {
            throw (Error) failure;
        }
        if (failure != null)
        {
            throw com.taitl.ex.logic.diagnostics.OperationalDiagnostics.failure(
                    com.taitl.existential.diagnostics.DiagnosticCode.CLEANUP_FAILED, "Event handler failed", null,
                    failure);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> void callEventHandler(EventHandler<T> handler, Event<T> event)
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
    protected void callBiHandler(BiEventHandler handler, Event<?> event)
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
    protected <T> void callExpression(Expression<T> expression, Event<T> event)
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

    protected void routeHandlerException(ExistentialException ex, ValidationReport report)
    {
        onException.call(ex, report);
    }
}
