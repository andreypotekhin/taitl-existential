package com.taitl.ex.logic.evaluation.intents.maps;

import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class ToIntentHandlers
{
    public <T> List<EventHandler<?>> call(
            MultiKey<T> multiKey,
            List<RuntimeKey<T>> runtimeKeys,
            EventType eventType,
            EventField field,
            Tr tr)
    {
        sane(multiKey, "multiKey", runtimeKeys, "runtimeKeys", eventType, "eventType", field, "field", tr,
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
                    tr.intentHandlers(eventType, runtimeKey.typeKey().toString());
            if (transactionHandlers != null)
            {
                result.addAll(transactionHandlers);
            }
        }
        return result;
    }
}
