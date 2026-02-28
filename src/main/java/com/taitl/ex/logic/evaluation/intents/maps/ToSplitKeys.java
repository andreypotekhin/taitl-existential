package com.taitl.ex.logic.evaluation.intents.maps;

import com.taitl.ex.logic.evaluation.split_events.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class ToSplitKeys
{
    protected final SplitTypeKey splitTypeKey;

    public ToSplitKeys()
    {
        this(new SplitTypeKey());
    }

    protected ToSplitKeys(SplitTypeKey splitTypeKey)
    {
        sane(splitTypeKey, "splitTypeKey");
        this.splitTypeKey = splitTypeKey;
    }

    public <T> Map<String, List<RuntimeKey<T>>> call(Set<RuntimeKey<T>> runtimeKeys)
    {
        return groupByEventType(runtimeKeys);
    }

    public <T> Map<String, List<RuntimeKey<T>>> groupByEventType(Set<RuntimeKey<T>> runtimeKeys)
    {
        sane(runtimeKeys, "runtimeKeys");
        Map<String, List<RuntimeKey<T>>> grouped = new LinkedHashMap<>();
        for (RuntimeKey<T> runtimeKey : runtimeKeys)
        {
            grouped.computeIfAbsent(eventTypeName(runtimeKey), ignored -> new ArrayList<>()).add(runtimeKey);
        }
        return grouped;
    }

    protected <T> String eventTypeName(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        return splitTypeKey.root(runtimeKey.key().toString());
    }
}
