package com.taitl.ex.logic.configuration.indexes;

import com.taitl.ex.logic.configuration.indexes.actions.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.configuration.rules.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

public class ConfigurationIndexes
{
    public ConfiguredHandlers configuredHandlers;
    protected EventField eventField;
    protected IndexConfig indexConfig;
    protected boolean useFullClassNames;
    public MaintainGlobalOrder maintainGlobalOrder;

    public ConfigurationIndexes()
    {
        this.maintainGlobalOrder = new MaintainGlobalOrder();
        this.configuredHandlers = new ConfiguredHandlers(this);
        this.indexConfig = new IndexConfig(this);
        this.eventField = new EventField(this);
    }

    /**
     * Add all configured rules to indexes, in the order of declaration.
     */
    public void indexConfig(String op, Config config)
    {
        sane(op, "op", config, "config");
        indexConfig.call(op, config);
    }

    /**
     * Adds event handler to the indexes.
     * Called from IndexConfig.
     */
    public <T> void addHandler(EventKey<?> eventKey, Ev<T> ev)
    {
        configuredHandlers.put(eventKey, ev);
    }

    /**
     * Marks indexes 'ready' for use.
     * Called from IndexConfig.
     */
    public void doneIndexing()
    {
        configuredHandlers.ready(true);
    }

    /* Attributes */

    public EventField eventField()
    {
        return eventField;
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
    }
}
