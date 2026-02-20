package com.taitl.ex.logic.configuration.indexes;

import java.util.*;
import com.taitl.ex.logic.configuration.indexes.actions.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.expressions.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

public class ConfigIndexes
{
    ConfiguredEventKeys configuredEventKeys;
    ConfiguredEventHandlers configuredEventHandlers;
    ConfiguredHandlers configuredHandlers;
    BitSet eventTypesMask;

    public ConfigIndexes()
    {
        this.configuredEventKeys = new ConfiguredEventKeys();
        this.configuredEventHandlers = new ConfiguredEventHandlers();
        this.configuredHandlers = new ConfiguredHandlers();
        this.eventTypesMask = new BitSet(64);
    }

    public ConfiguredEventKeys eventKeys()
    {
        return configuredEventKeys;
    }

    public BitSet eventTypeMask()
    {
        return eventTypesMask;
    }

    /**
     * Add all configured rules to indexes, in the order of declaration.
     */
    public void indexConfig(Config config)
    {
        TraverseConfig tc = new TraverseConfig(this);
        tc.visit(config);
    }

    public void onContext(Context context)
    {
        TraverseContext tc = new TraverseContext(this);
        tc.visit(context);
    }

    public <T> void onRule(Ev<T> ev)
    {
        // Bug: we need to know a TypeKey to create a proper EventKey
        // Without it, it is just 'Event'!
        EventKey eventKey = new EventKey(ev);
        if (ev instanceof EventHandler<T> handler)
        {
            configuredEventHandlers.put(eventKey, handler);
        }
        else if (ev instanceof Expression<T> expression)
        {
            // TODO: add to expression index, if needed
        }
        else
        {
            throw new RuntimeException("Unknown rule type: " + ev.getClass());
        }
        configuredHandlers.put(eventKey, ev);
    }

    public void close()
    {
        configuredHandlers.clear();
        configuredEventHandlers.clear();
        configuredEventKeys.clear();
        eventTypesMask = null;
    }
}
