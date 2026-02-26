package com.taitl.ex.logic.evaluation.actions;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.evaluation.logic.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;

import java.util.*;

/**
 * Maps incoming compound event to a set of basic, fine-grained events that
 * constraints may be attached to.
 * Example:
 * Incoming event: ex.event(null, cat, tr);
 * Resulting events: Transit<Cat>, Create<Cat>
 * Incoming event: ex.event(cat, null, tr);
 * Resulting events: Delete(cat)
 */
// TODO: process events specified by EventKey
public class SplitEvent
{
    protected EventSplitter eventSplitter = Creator.singleton(EventSplitter.class);

    public <T> Set<Event<T>> split(Event<T> event)
    {
        return eventSplitter.split(event);
    }

    public <T> Set<RuntimeKey<T>> split(RuntimeKey<T> runtimeKey)
    {
        return eventSplitter.split(runtimeKey);
    }

}
