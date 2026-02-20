package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.expressions.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

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
        public <T> void visit(Ev<T> ev)
        {
            // Bug: we need to know a TypeKey to create a proper EventKey
            // Without it, it is just 'Event'!
            EventKey eventKey = new EventKey(ev);
            if (ev instanceof EventHandler<T> handler)
            {
                ci.configuredEventHandlers.put(eventKey, handler);
            }
            else if (ev instanceof Expression<T> expression)
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
    }
}
