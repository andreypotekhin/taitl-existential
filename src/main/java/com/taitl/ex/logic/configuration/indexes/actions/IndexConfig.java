package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.ex.logic.configuration.indexes.ConfigIndexes;
import com.taitl.existential.configs.Config;
import com.taitl.existential.configs.Context;
import com.taitl.existential.evaluables.Ev;
import com.taitl.existential.evaluables.Evaluator;
import com.taitl.existential.evaluables.Evs;
import com.taitl.existential.expressions.Expression;
import com.taitl.existential.handlers.types.EventHandler;
import com.taitl.existential.keys.EventKey;
import com.taitl.existential.keys.TypeKey;

import static com.taitl.ex.common.helper.Args.sane;

public class IndexConfig
{
    protected ConfigIndexes ci;

    public IndexConfig(ConfigIndexes ci)
    {
        this.ci = ci;
    }

    public void call(String op, Config config)
    {
        sane(op, "op", config, "config");
        indexConfig(config);
    }

    /**
     * Add all configured rules to indexes, in the order of declaration.
     */
    public void indexConfig(Config config)
    {
        for (Context context : config.contexts())
        {
            TraverseContext tc = new TraverseContext();
            tc.visit(context);
        }
    }

    class TraverseContext implements Evaluator
    {
        protected TypeKey<?> currentTypeKey;

        @Override
        public <T> void visit(Evs<T> evs)
        {
            TypeKey<?> previous = currentTypeKey;
            currentTypeKey = evs.typeKey();
            try
            {
                Evaluator.super.visit(evs);
            }
            finally
            {
                currentTypeKey = previous;
            }
        }

        @Override
        public <T> void visit(Ev<T> ev)
        {
            EventKey eventKey = eventKey(ev);
            if (ev instanceof EventHandler<?>)
            {
                ci.configuredEventHandlers.put(eventKey, (EventHandler<T>) ev);
            }
            else if (ev instanceof Expression<?>)
            {
                // TODO: add to expression index, if needed
            }
            else
            {
                throw new RuntimeException("Unknown rule type: " + ev.getClass());
            }
            ci.configuredHandlers.put(eventKey, ev);

            // TODO: add to EventField
        }

        protected <T> EventKey eventKey(Ev<T> ev)
        {
            EventKey typed = typedEventKey(ev);
            if (typed != null)
            {
                return typed;
            }
            return EventKey.valueOf(ev, ci.useFullClassNames());
        }

        @SuppressWarnings("unchecked")
        protected <T> EventKey typedEventKey(Ev<T> ev)
        {
            if (!(ev instanceof EventHandler<?>))
            {
                return null;
            }
            EventHandler<T> handler = (EventHandler<T>) ev;
            TypeKey<T> typeKey = (TypeKey<T>) currentTypeKey;
            Class<?> eventClass = handler.eventType().eventClass();
            return ci.useFullClassNames() ? EventKey.valueOfFull(eventClass, typeKey)
                    : EventKey.valueOf(eventClass, typeKey);
        }
    }
}
