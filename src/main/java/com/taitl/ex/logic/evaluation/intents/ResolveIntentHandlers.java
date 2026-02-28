package com.taitl.ex.logic.evaluation.intents;

import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class ResolveIntentHandlers
{
    public boolean eventTypeIsGuarded(String eventTypeName, ConfigurationIndexes indexes, Tr tr)
    {
        sane(eventTypeName, "eventTypeName", indexes, "indexes", tr, "tr");
        return indexes.hasIntentEventType(eventTypeName) || tr.hasIntentEventType(eventTypeName);
    }

    public <T> List<EventHandler<?>> call(
            MultiKey<T> multiKey,
            List<RuntimeKey<T>> runtimeKeys,
            String eventTypeName,
            EventField field,
            Tr tr)
    {
        sane(multiKey, "multiKey", runtimeKeys, "runtimeKeys", eventTypeName, "eventTypeName", field, "field", tr,
                "tr");

        List<EventHandler<?>> result = new ArrayList<>();
        for (Ev<?> ev : field.get(multiKey))
        {
            if (ev instanceof EventHandler<?>)
            {
                result.add((EventHandler<?>) ev);
            }
        }

        for (RuntimeKey<T> runtimeKey : runtimeKeys)
        {
            List<EventHandler<?>> transactionHandlers =
                    tr.intentHandlers(eventTypeName, runtimeKey.typeKey().toString());
            if (transactionHandlers != null)
            {
                result.addAll(transactionHandlers);
            }
        }
        return result;
    }
}
