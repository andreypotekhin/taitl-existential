package com.taitl.ex.logic.events.actions;

import java.util.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.events.logic.*;
import com.taitl.existential.events.types.*;

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

}
