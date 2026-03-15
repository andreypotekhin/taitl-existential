package com.taitl.ex.logic.evaluation.events.split_events;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.events.split_events.data.*;
import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;
import com.taitl.ex.logic.evaluation.events.split_events.maps.*;
import com.taitl.ex.logic.evaluation.events.split_events.rules.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.stream.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Maps an incoming compound event to a set of basic, fine-grained events
 * for which constraints may have been configured.
 *
 * Example:
 * Incoming event: ex.transit(null, cat, tr);
 * Resulting events: Port<Cat>, Create<Cat>
 * Incoming event: ex.transit(doc, null, tr); // (doc type: Doc<JSON>)
 * Resulting events: Delete<Doc<JSON>>, Delete<Doc<?>, Delete<Doc>
 *
 * // TODO: also need more general events from elementary:
 * Create -> Create, CUD, CU, Port
 * Update -> Update, CUD, CU, Transit
 * Delete -> Delete, CUD, UD, Port
 */
public class SplitEvent
{
    // TODO: source from ExistentialEvents instead of singleton
    @Logic
    protected EventSplitter eventSplitter = Creator.singleton(EventSplitter.class);

    @Logic
    protected RequireMemoForBiEvents requireMemoForBiEvents = Creator.create(RequireMemoForBiEvents.class);

    @Logic
    protected ToMemo toMemo = Creator.create(ToMemo.class);

    public <T> SplitResult<T> call(
            RuntimeKey<T> runtimeKey,
            EventField eventField,
            boolean useFullEventNames,
            boolean splitElementaryToCompound,
            Tr tr)
            throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", eventField, "eventField");
        if (tr != null)
        {
            requireMemoForBiEvents.forHandlers(runtimeKey, eventField, useFullEventNames, tr);
        }
        Set<RuntimeKey<T>> splitKeys =
                eventSplitter.split(runtimeKey, useFullEventNames, splitElementaryToCompound, tr);
        MultiKey<T> multiKey = multiKey(splitKeys);
        List<Ev<T>> evaluables = eventField.get(multiKey);
        Event<T> event = tr != null ? toMemo.forExecution(runtimeKey, evaluables, tr) : runtimeKey.event();
        return new SplitResult<T>(evaluables, event);
    }

    protected <T> MultiKey<T> multiKey(Set<RuntimeKey<T>> splitKeys)
    {
        sane(splitKeys, "splitKeys");
        List<EventKey<T>> eventKeys = splitKeys.stream().map(RuntimeKey::key).collect(Collectors.toList());
        return MultiKey.valueOf(eventKeys);
    }
}
