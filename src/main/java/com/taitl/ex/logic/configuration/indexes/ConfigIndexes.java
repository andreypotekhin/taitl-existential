package com.taitl.ex.logic.configuration.indexes;

import java.util.*;
import com.taitl.ex.logic.configuration.indexes.actions.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

public class ConfigIndexes
{
    ConfiguredEventKeys configuredEventKeys;
    public ConfiguredEventHandlers configuredEventHandlers;
    public ConfiguredHandlers configuredHandlers;
    BitSet eventTypesMask;
    IndexConfig indexConfig;

    public ConfigIndexes()
    {
        this.configuredEventKeys = new ConfiguredEventKeys();
        this.configuredEventHandlers = new ConfiguredEventHandlers();
        this.configuredHandlers = new ConfiguredHandlers();
        this.eventTypesMask = new BitSet(64);
        this.indexConfig = new IndexConfig(this);
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
    public void indexConfig(String op, Config config)
    {
        sane(op, "op", config, "config");
        indexConfig.call(op, config);
    }

    public void close()
    {
        configuredHandlers.clear();
        configuredEventHandlers.clear();
        configuredEventKeys.clear();
        eventTypesMask = null;
    }
}
