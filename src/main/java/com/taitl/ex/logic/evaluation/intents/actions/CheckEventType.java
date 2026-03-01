package com.taitl.ex.logic.evaluation.intents.actions;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class CheckEventType
{
    public boolean eventTypeIsGuarded(String eventTypeName, ConfigurationIndexes indexes, Tr tr)
    {
        sane(eventTypeName, "eventTypeName", indexes, "indexes", tr, "tr");
        return indexes.hasIntentEventType(eventTypeName) || tr.hasIntentEventType(eventTypeName);
    }
}
