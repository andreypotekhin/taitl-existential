package com.taitl.ex.logic.evaluation.intents.actions;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class CheckEventType
{
    public boolean eventTypeIsGuarded(EventType eventType, ConfigurationIndexes indexes, Tr tr, StageName stageName)
    {
        sane(eventType, "eventTypeName", indexes, "indexes", tr, "tr", stageName, "stageName");
        return indexes.hasIntentEventType(eventType) || tr.hasIntentEventType(stageName, eventType);
    }
}
