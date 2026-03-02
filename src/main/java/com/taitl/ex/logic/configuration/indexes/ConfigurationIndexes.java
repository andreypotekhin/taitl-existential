package com.taitl.ex.logic.configuration.indexes;

import com.taitl.ex.logic.configuration.indexes.actions.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.configuration.rules.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class ConfigurationIndexes
{
    public ConfiguredHandlers configuredHandlers;
    public ConfiguredHandlers configuredIntents;
    protected EventField eventField;
    protected EventField intentField;
    protected IndexConfig indexConfig;
    protected boolean useFullClassNames;
    public MaintainGlobalOrder maintainGlobalOrder;
    protected Set<EventType> intentEventTypes = new LinkedHashSet<>();

    public ConfigurationIndexes()
    {
        this.maintainGlobalOrder = new MaintainGlobalOrder();
        this.configuredHandlers = new ConfiguredHandlers(this);
        this.configuredIntents = new ConfiguredHandlers(this);
        this.indexConfig = new IndexConfig(this);
        this.eventField = new EventField(this, configuredHandlers);
        this.intentField = new EventField(this, configuredIntents);
    }

    /**
     * Add all configured rules to indexes, in the order of declaration.
     */
    public void indexConfig(String op, Config config)
    {
        sane(op, "op", config, "config");
        indexConfig.call(op, config, StageName.VALIDATION);
    }

    public void indexConfig(String op, Config config, StageName stageName)
    {
        sane(op, "op", config, "config", stageName, "stageName");
        indexConfig.call(op, config, stageName);
    }

    /**
     * Adds event handler to the indexes.
     * Called from IndexConfig.
     */
    public <T> void addHandler(EventKey<T> eventKey, Ev<T> ev)
    {
        configuredHandlers.put(eventKey, ev);
    }

    public <T> void addIntent(EventKey<T> eventKey, Ev<T> ev)
    {
        configuredIntents.put(eventKey, ev);
    }

    public void addIntentEventType(EventType eventType)
    {
        sane(eventType, "eventType");
        intentEventTypes.add(eventType);
    }

    public boolean hasIntentEventType(EventType eventType)
    {
        sane(eventType, "eventType");
        return intentEventTypes.contains(eventType);
    }

    public boolean hasIntentEventTypes()
    {
        return !intentEventTypes.isEmpty();
    }

    /**
     * Marks indexes 'ready' for use.
     * Called from IndexConfig.
     */
    public void doneIndexing()
    {
        configuredHandlers.ready(true);
        configuredIntents.ready(true);
    }

    /* Attributes */

    public EventField eventField()
    {
        return eventField;
    }

    public EventField intentField()
    {
        return intentField;
    }

    public boolean useFullClassNames()
    {
        return useFullClassNames;
    }

    public void useFullClassNames(boolean useFullClassNames)
    {
        this.useFullClassNames = useFullClassNames;
    }

    public void close()
    {
        configuredHandlers.clear();
        configuredIntents.clear();
        intentEventTypes.clear();
    }
}
