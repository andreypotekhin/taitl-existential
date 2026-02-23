package com.taitl.ex.logic.configuration.indexes;

import java.util.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.actions.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

public class ConfigIndexes
{
    boolean useFullClassNames;
    ConfiguredEventKeys configuredEventKeys;
    public ConfiguredEventHandlers configuredEventHandlers;
    public ConfiguredHandlers configuredHandlers;
    BitSet eventTypesMask;
    IndexConfig indexConfig;
    public EventField eventField;

    public ConfigIndexes()
    {
        this.configuredEventKeys = new ConfiguredEventKeys();
        this.configuredEventHandlers = new ConfiguredEventHandlers();
        this.configuredHandlers = new ConfiguredHandlers();
        this.eventTypesMask = new BitSet(64);
        this.indexConfig = new IndexConfig(this);
        this.eventField = Creator.create(EventField.class);
    }

    /**
     * Add all configured rules to indexes, in the order of declaration.
     */
    public void indexConfig(String op, Config config)
    {
        sane(op, "op", config, "config");
        indexConfig.call(op, config);
    }

    public ConfiguredEventKeys eventKeys()
    {
        return configuredEventKeys;
    }

    public BitSet eventTypeMask()
    {
        return eventTypesMask;
    }

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
        configuredEventHandlers.clear();
        configuredEventKeys.clear();
        eventTypesMask = null;
    }
}
