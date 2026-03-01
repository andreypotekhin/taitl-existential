package com.taitl.ex.logic.evaluation.split_events;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.stream.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Maps an incoming compound event to a set of basic, fine-grained events
 * for which constraints may have been configured.
 *
 * Example:
 * Incoming event: ex.event(null, cat, tr);
 * Resulting events: Transit<Cat>, Create<Cat>
 * Incoming event: ex.event(doc, null, tr); // (doc type: Doc<JSON>)
 * Resulting events: Delete<Doc<JSON>>, Delete<Doc<?>, Delete<Doc>
 */
public class SplitEvent
{
    protected EventSplitter eventSplitter = Creator.singleton(EventSplitter.class);

    public <T> SplitResult call(RuntimeKey<T> runtimeKey, EventField eventField)
    {
        return call(runtimeKey, eventField, false);
    }

    public <T> SplitResult call(RuntimeKey<T> runtimeKey, EventField eventField, boolean useFullEventNames)
    {
        sane(runtimeKey, "runtimeKey", eventField, "eventField");
        Set<RuntimeKey<T>> splitKeys = split(runtimeKey, useFullEventNames);
        MultiKey<T> multiKey = multiKey(splitKeys);
        List<Ev<?>> evaluables = eventField.get(multiKey);
        return new SplitResult(evaluables, runtimeKey.event());
    }

    protected <T> Set<RuntimeKey<T>> split(RuntimeKey<T> runtimeKey)
    {
        return split(runtimeKey, false);
    }

    protected <T> Set<RuntimeKey<T>> split(RuntimeKey<T> runtimeKey, boolean useFullEventNames)
    {
        return eventSplitter.split(runtimeKey, useFullEventNames);
    }

    protected <T> MultiKey<T> multiKey(Set<RuntimeKey<T>> splitKeys)
    {
        sane(splitKeys, "splitKeys");
        List<EventKey<T>> eventKeys = splitKeys.stream().map(RuntimeKey::key).collect(Collectors.toList());
        return MultiKey.valueOf(eventKeys);
    }
}
