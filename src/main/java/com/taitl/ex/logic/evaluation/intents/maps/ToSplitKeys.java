package com.taitl.ex.logic.evaluation.intents.maps;

import com.taitl.ex.logic.evaluation.split_events.*;
import com.taitl.existential.events.types.*;
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

    public <T> Map<EventType, List<RuntimeKey<T>>> call(Set<RuntimeKey<T>> runtimeKeys)
    {
        return groupByEventType(runtimeKeys);
    }

    public <T> Map<EventType, List<RuntimeKey<T>>> groupByEventType(Set<RuntimeKey<T>> runtimeKeys)
    {
        sane(runtimeKeys, "runtimeKeys");
        Map<EventType, List<RuntimeKey<T>>> grouped = new LinkedHashMap<>();
        for (RuntimeKey<T> runtimeKey : runtimeKeys)
        {
            grouped.computeIfAbsent(eventType(runtimeKey), ignored -> new ArrayList<>()).add(runtimeKey);
        }
        return grouped;
    }

    protected <T> EventType eventType(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        Event<T> event = runtimeKey.event();
        check(event != null, "Runtime key event should not be null");
        return EventType.valueOf(event.getClass());
    }
}
