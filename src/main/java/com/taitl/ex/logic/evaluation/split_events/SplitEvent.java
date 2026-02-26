package com.taitl.ex.logic.evaluation.split_events;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;

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

    public SplitResult call(RuntimeKey<?> runtimeKey, EventField eventField)
    {
        sane(runtimeKey, "runtimeKey", eventField, "eventField");
        Set<RuntimeKey<?>> splitKeys = split(runtimeKey);
        MultiKey multiKey = multiKey(splitKeys);
        List<Ev<?>> evs = eventField.get(multiKey);
        return new SplitResult(evs, runtimeKey.event());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Set<RuntimeKey<?>> split(RuntimeKey<?> runtimeKey)
    {
        return (Set) eventSplitter.split(runtimeKey);
    }

    protected MultiKey multiKey(Set<RuntimeKey<?>> splitKeys)
    {
        sane(splitKeys, "splitKeys");
        EventKey[] eventKeys = splitKeys.stream().map(RuntimeKey::key).toArray(EventKey[]::new);
        return MultiKey.valueOf(eventKeys);
    }
}
