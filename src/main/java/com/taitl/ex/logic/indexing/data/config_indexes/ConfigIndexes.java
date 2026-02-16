package com.taitl.ex.logic.indexing.data.config_indexes;

import java.util.*;

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

    public void close()
    {
        configuredEventKeys = null;
        eventTypesMask = null;
    }
}
