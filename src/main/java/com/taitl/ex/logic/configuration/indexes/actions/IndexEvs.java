package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.types.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Indexes a single typed rule set into {@link ConfigurationIndexes}.
 */
public class IndexEvs implements Evaluator
{
    @Up
    protected ConfigurationIndexes ci;

    protected TypeKey<?> currentTypeKey;
    protected boolean currentIntent;
    protected boolean currentEffect;
    protected String context;
    protected boolean transactionLocal;

    @Logic
    protected TypeMatchingLogic typeMatchingLogic;

    public IndexEvs(ConfigurationIndexes ci)
    {
        sane(ci, "ci");
        this.ci = ci;
        this.typeMatchingLogic = Creator.create(TypeMatchingLogic.class);
    }

    public <T> void call(Evs<T> evs, String context, boolean transactionLocal)
    {
        this.context = context;
        this.transactionLocal = transactionLocal;
        try
        {
            call(evs);
        }
        finally
        {
            this.context = null;
            this.transactionLocal = false;
        }
    }

    public <T> void call(Evs<T> evs)
    {
        sane(evs, "evs");
        evs.accept(this);
    }

    public <T> void visit(Evs<T> evs)
    {
        TypeKey<?> previous = currentTypeKey;
        boolean previousIntent = currentIntent;
        boolean previousEffect = currentEffect;
        currentTypeKey = evs.typeKey();
        currentIntent = evs instanceof Intent<?>;
        currentEffect = evs instanceof Effect<?>;
        try
        {
            if (currentTypeKey != null)
            {
                typeMatchingLogic.validateRuleType(currentTypeKey);
            }
            Evaluator.super.visit(evs);
        }
        finally
        {
            currentTypeKey = previous;
            currentIntent = previousIntent;
            currentEffect = previousEffect;
        }
    }

    public <T> void visit(Ev<T> ev)
    {
        if (ev instanceof EventHandler<?>)
        {
            // event handlers are indexed below via eventKey(ev)
        }
        else if (!(ev instanceof Expression<?>))
        {
            throw new RuntimeException("Unknown rule type: " + ev.getClass());
        }

        EventKey<T> eventKey = eventKey(ev);
        String description = ev instanceof EventHandler<?> ? ((EventHandler<?>) ev).description()
                : ((Expression<?>) ev).description();
        com.taitl.ex.logic.diagnostics.data.RuleOrigin origin =
                new com.taitl.ex.logic.diagnostics.data.RuleOrigin(description, context, transactionLocal,
                        eventKey.toString());
        if (currentIntent)
        {
            ci.configuredIntents.put(eventKey, ev, false, origin);
            ci.addIntentEventType(eventType(ev));
            if (biEventType(ev))
            {
                ci.addBiKey(eventKey);
            }
            return;
        }

        ci.configuredHandlers.put(eventKey, ev, currentEffect, origin);
        if (biEventType(ev))
        {
            ci.addBiKey(eventKey);
        }
    }

    protected <T> EventKey<T> eventKey(Ev<T> ev)
    {
        EventKey<T> typed = typedEventKey(ev);
        if (typed != null)
        {
            return typed;
        }
        TypeKey<T> typeKey = currentTypeKey(ev);
        return useFullEventNames() ? EventKey.valueOfFull(Expression.class, typeKey)
                : EventKey.valueOf(Expression.class, typeKey);
    }

    @SuppressWarnings("unchecked")
    protected <T> TypeKey<T> currentTypeKey(Ev<T> ev)
    {
        if (currentTypeKey != null)
        {
            return (TypeKey<T>) currentTypeKey;
        }
        return (TypeKey<T>) TypeKey.valueOf(ev, ci.useFullClassNames());
    }

    @SuppressWarnings("unchecked")
    protected <T> EventKey<T> typedEventKey(Ev<T> ev)
    {
        if (!(ev instanceof EventHandler<?>))
        {
            return null;
        }
        EventHandler<T> handler = (EventHandler<T>) ev;
        TypeKey<T> typeKey = (TypeKey<T>) currentTypeKey;
        Class<?> eventClass = handler.eventType().eventClass();
        return useFullEventNames() ? EventKey.valueOfFull(eventClass, typeKey)
                : EventKey.valueOf(eventClass, typeKey);
    }

    @SuppressWarnings("unchecked")
    protected <T> EventType eventType(Ev<T> ev)
    {
        if (!(ev instanceof EventHandler<?>))
        {
            throw new IllegalStateException("Intent contains a non-handler rule: " + ev.getClass());
        }
        return ((EventHandler<T>) ev).eventType();
    }

    protected boolean useFullEventNames()
    {
        return !currentIntent && ci.useFullClassNames();
    }

    protected <T> boolean biEventType(Ev<T> ev)
    {
        if (!(ev instanceof EventHandler<?>))
        {
            return false;
        }
        return eventType(ev).biEvent();
    }
}
