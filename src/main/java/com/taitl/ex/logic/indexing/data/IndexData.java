package com.taitl.ex.logic.indexing.data;

import java.util.*;
import com.taitl.ex.logic.indexing.data.config_indexes.*;
import com.taitl.ex.logic.indexing.data.runtime_indexes.*;
import com.taitl.ex.logic.validation.data.*;
import com.taitl.existential.transactions.*;

/**
 * Stores the results of indexing process for use in rule evaluations
 * by various processing stages (runtime, validation).
 * Includes configuration and runtime indexes to help with efficient rule evaluation.
 * The data in this class is scoped to a single transaction.
 * An instance of IndexData is owned by Tr class (Tr.indexData).
 * Used by ValidationData.
 *
 * @see Tr
 * @see ValidationData
 */
public class IndexData
{
    EventKeys eventKeys;
    EventHandlers<?> eventHandlers;
    BitSet eventTypesMask;
    EventField eventField;

    public IndexData()
    {
        this.eventKeys = new EventKeys();
        this.eventHandlers = new EventHandlers<>();
        this.eventField = new EventField();
        this.eventTypesMask = new BitSet(64);
    }

    public EventKeys eventKeys()
    {
        return eventKeys;
    }

    public BitSet eventTypeMask()
    {
        return eventTypesMask;
    }

    public void close()
    {
        eventKeys = null;
        eventTypesMask = null;
    }
}
