package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;
import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys.*;
import com.taitl.ex.logic.evaluation.events.split_events.maps.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.ex.logic.types.data.*;
import com.taitl.existential.events.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Builds the event-family and structural-type facts needed after a selection cache miss.
 * Event splitting and memo availability determine which families can execute for this shape;
 * General Event handlers and Expression remain candidates alongside concrete event families.
 *
 * Missing-memo admission retains the existing declared-subject scope, while available payloads
 * can select inherited rules. Type resolution delegates to ResolveReportedTypes rather than
 * expanding wildcard combinations. Only metadata enters the input; execution obtains each
 * entity's actual before/after values separately.
 */
public class ResolveSelectionInput
{
    @Logic
    protected EventSplitter eventSplitter = Creator.singleton(EventSplitter.class);

    @Logic
    protected ResolveReportedTypes resolveReportedTypes = Creator.create(ResolveReportedTypes.class);

    @Logic
    protected ToMemo toMemo = Creator.create(ToMemo.class);

    public <T> SelectionInput call(RuntimeKey<T> key, boolean fullNames, boolean split, Tr tr)
    {
        Set<String> events = new LinkedHashSet<>();
        for (Event<T> event : eventSplitter.splitEvent(key.event(), split))
        {
            if (tr == null || available(event, key, tr))
            {
                events.add(name(event.getClass(), fullNames));
            }
        }
        events.add(name(Event.class, fullNames));
        events.add(name(Expression.class, fullNames));
        Set<String> memoEvents = new LinkedHashSet<>();
        if (tr != null && toMemo.memoSensitive(key) && !tr.hasMemo(key.entity(), key.typeKey()))
        {
            if (key.event() instanceof Delete<?> || key.event() instanceof UD<?> || key.event() instanceof CUD<?>)
            {
                memoEvents.add(name(Port.class, fullNames));
            }
            if (!(key.event() instanceof Delete<?>))
            {
                memoEvents.add(name(Transit.class, fullNames));
            }
            if (key.event() instanceof CU<?>)
            {
                memoEvents.add(name(Port.class, fullNames));
            }
        }
        Set<TypePattern> types = types(key, fullNames);
        SelectionInput input = new SelectionInput(types, events, memoEvents);
        input.memoTypes = new LinkedHashSet<>();
        String raw = new TypePattern(key.typeKey().toString()).raw();
        for (TypePattern type : types)
        {
            if (type.raw().equals(raw))
            {
                input.memoTypes.add(type);
            }
        }
        return input;
    }

    protected <T> boolean available(Event<T> event, RuntimeKey<T> key, Tr tr)
    {
        try
        {
            return toMemo.forSplit(event, key, tr) != null;
        }
        catch (ExistentialException failure)
        {
            throw new IllegalStateException("Cannot resolve event shape for " + key.key(), failure);
        }
    }

    public Set<TypePattern> types(RuntimeKey<?> key, boolean fullNames)
    {
        return resolveReportedTypes.call(key, fullNames);
    }

    protected String name(Class<?> type, boolean fullNames)
    {
        return fullNames ? type.getName() : type.getSimpleName();
    }
}
