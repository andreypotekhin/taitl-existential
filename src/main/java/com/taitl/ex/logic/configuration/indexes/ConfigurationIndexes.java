package com.taitl.ex.logic.configuration.indexes;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
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

/**
 * Indexes supporting fast retrieval and processing of rules configured per
 * event type during processing stages such as immediate, validation.
 *
 * Instances of this class are owned by:
 * - Config class: stageIndexes map, per processing stage, for the rules configured for Contexts
 * - PreparedEventFields class: indexes map, per processing stage, for the rules configured for Transaction
 *
 * @see com.taitl.existential.configs.Config
 * @see com.taitl.ex.logic.tr.data.EventFields
 * @see com.taitl.existential.configs.Transaction
 */
public class ConfigurationIndexes
{
    @Logic
    public ConfiguredHandlers configuredHandlers;

    @Logic
    public ConfiguredHandlers configuredIntents;

    @Logic
    protected IndexConfig indexConfig;

    @Logic
    public MaintainGlobalOrder maintainGlobalOrder;

    protected EventField eventField;
    protected EventField intentField;
    protected Set<String> biKeys = new LinkedHashSet<>();
    protected Set<EventType> intentEventTypes = new LinkedHashSet<>();
    protected boolean useFullClassNames;

    public ConfigurationIndexes()
    {
        this.maintainGlobalOrder = Creator.create(MaintainGlobalOrder.class);
        this.configuredHandlers =
                Creator.create(ConfiguredHandlers.class, new Class[] { ConfigurationIndexes.class }, this);
        this.configuredIntents =
                Creator.create(ConfiguredHandlers.class, new Class[] { ConfigurationIndexes.class }, this);
        this.indexConfig = Creator.create(IndexConfig.class, new Class[] { ConfigurationIndexes.class }, this);
        this.eventField = Creator.create(EventField.class,
                new Class[] { ConfigurationIndexes.class, ConfiguredHandlers.class }, this, configuredHandlers);
        this.intentField = Creator.create(EventField.class,
                new Class[] { ConfigurationIndexes.class, ConfiguredHandlers.class }, this, configuredIntents);
    }

    /**
     * Add all configured rules to indexes, in the order of declaration.
     */
    public void indexConfig(String op, Config config, StageName stageName)
    {
        sane(op, "op", config, "config", stageName, "stageName");
        requireConcreteOp(op);
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

    public <T> void addBiKey(EventKey<T> eventKey)
    {
        sane(eventKey, "eventKey");
        biKeys.add(eventKey.toString());
    }

    public boolean hasIntentEventType(EventType eventType)
    {
        sane(eventType, "eventType");
        return intentEventTypes.contains(eventType);
    }

    public boolean hasIntents()
    {
        return !intentEventTypes.isEmpty();
    }

    public <T> boolean hasBiKey(EventKey<T> eventKey)
    {
        sane(eventKey, "eventKey");
        return biKeys.contains(eventKey.toString());
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
        biKeys.clear();
        intentEventTypes.clear();
    }

    protected void requireConcreteOp(String op)
    {
        sane(op, "op");
        ContextKey.validate(op);
        check(!op.contains("*"),
                String.format("Cannot index wildcard context '%s': transaction operation keys must be concrete", op));
    }
}
