package com.taitl.ex.logic.evaluation.intents.maps;

import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class IntentTypeCandidates
{
    public <T> List<String> call(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        return call(runtimeKey.typeKey(), runtimeKey.entity());
    }

    public <T> List<String> call(TypeKey<T> typeKey, T entity)
    {
        sane(typeKey, "typeKey");
        Set<String> result = new LinkedHashSet<>();
        result.add(typeKey.toString());
        if (entity != null)
        {
            result.add(entity.getClass().getSimpleName());
            result.add(entity.getClass().getName());
        }
        return new ArrayList<>(result);
    }
}
