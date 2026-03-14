package com.taitl.ex.logic.evaluation.intents.maps;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class ToSplitKeys
{
    @Logic
    protected SplitTypeKey splitTypeKey;

    public ToSplitKeys()
    {
        this.splitTypeKey = Creator.create(SplitTypeKey.class);
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
        sane(event, "event");
        return EventType.valueOf(event.getClass());
    }
}
