package com.taitl.ex.logic.configuration.indexes;

import java.util.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.configs.*;

public class ConfigIndexes
{
    ConfiguredEventKeys configuredEventKeys;
    ConfiguredEventHandlers<?> configuredEventHandlers;
    BitSet eventTypesMask;

    public ConfigIndexes()
    {
        this.configuredEventKeys = new ConfiguredEventKeys();
        this.configuredEventHandlers = new ConfiguredEventHandlers<>();
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

    public void indexConfig(Config config)
    {
        // Add all configured rules to indexes, in the order of declaration
    }

    public void close()
    {
        configuredEventKeys = null;
        eventTypesMask = null;
    }
}
