package com.taitl.ex.logic.evaluation.intents.maps;

import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.constants.*;
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
            Tr tr,
            StageName stageName)
    {
        sane(multiKey, "multiKey", runtimeKeys, "runtimeKeys", eventType, "eventType", field, "field", tr,
                "tr", stageName, "stageName");

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
            for (String candidate : intentTypeCandidates(runtimeKey))
            {
                List<EventHandler<?>> transactionHandlers = tr.intentHandlers(stageName, eventType, candidate);
                if (transactionHandlers != null)
                {
                    result.addAll(transactionHandlers);
                }
            }
        }
        return result;
    }

    protected <T> List<String> intentTypeCandidates(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        List<String> result = new ArrayList<>();
        result.add(runtimeKey.typeKey().toString());
        T entity = runtimeKey.entity();
        if (entity != null)
        {
            result.add(entity.getClass().getSimpleName());
            result.add(entity.getClass().getName());
        }
        return result;
    }
}
