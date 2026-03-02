package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.expressions.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.events.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class IndexConfig
{
    protected ConfigurationIndexes ci;

    public IndexConfig(ConfigurationIndexes ci)
    {
        this.ci = ci;
    }

    public void call(String op, Config config)
    {
        sane(op, "op", config, "config");
        indexConfig(config, StageName.VALIDATION);
    }

    public void call(String op, Config config, StageName stageName)
    {
        sane(op, "op", config, "config", stageName, "stageName");
        indexConfig(config, stageName);
    }

    /**
     * Add all configured rules to indexes, in the order of declaration.
     */
    public void indexConfig(Config config, StageName stageName)
    {
        sane(config, "config", stageName, "stageName");
        for (Context context : config.contexts())
        {
            TraverseContext tc = new TraverseContext();
            for (Evs<?> evs : context.stage().at(stageName))
            {
                evs.accept(tc);
            }
        }
        ci.doneIndexing();
    }

    class TraverseContext implements Evaluator
    {
        protected TypeKey<?> currentTypeKey;
        protected boolean currentIntent;

        public <T> void visit(Evs<T> evs)
        {
            TypeKey<?> previous = currentTypeKey;
            boolean previousIntent = currentIntent;
            currentTypeKey = evs.typeKey();
            currentIntent = evs instanceof Intent<?>;
            try
            {
                Evaluator.super.visit(evs);
            }
            finally
            {
                currentTypeKey = previous;
                currentIntent = previousIntent;
            }
        }

        public <T> void visit(Ev<T> ev)
        {
            if (ev instanceof EventHandler<?>)
            {
                // ci.configuredEventHandlers.put(eventKey, (EventHandler<T>) ev);
            }
            else if (ev instanceof Expression<?>)
            {
                // TODO: add to expression index, if needed
            }
            else
            {
                throw new RuntimeException("Unknown rule type: " + ev.getClass());
            }
            EventKey<T> eventKey = eventKey(ev);
            if (currentIntent)
            {
                ci.addIntent(eventKey, ev);
                ci.addIntentEventType(eventType(ev));
                return;
            }
            ci.addHandler(eventKey, ev);
        }

        protected <T> EventKey<T> eventKey(Ev<T> ev)
        {
            EventKey<T> typed = typedEventKey(ev);
            if (typed != null)
            {
                return typed;
            }
            TypeKey<T> typeKey = currentTypeKey(ev);
            return useFullEventNames() ? EventKey.valueOfFull(ev.getClass(), typeKey)
                    : EventKey.valueOf(ev.getClass(), typeKey);
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
    }
}
