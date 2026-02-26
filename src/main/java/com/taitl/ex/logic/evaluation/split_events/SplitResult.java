package com.taitl.ex.logic.evaluation.split_events;

import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Result of SplitEvent.call(): resolved event handlers plus the original event
 * used to derive runtime payload for handler execution.
 * The event handlers are ordered in the order of rules declaration.
 *
 * Example:
 * Main event: ReadAndLock<Doc<JSON>>
 * Split results:
 * Event handlers: event handlers for these keys, retrieved
 * from the configuration index (EventField) of this business operation,
 * ordered in the order of rules' declaration.
 *
 * Note that the order of declaration may differ from the above order of the keys.
 * For instance, if a constraint for Read<Doc> is declared before a constraint
 * for ReadAndLock<Doc<JSON>> (e.g. in a parent context), it will be evaluated first.
 */
public class SplitResult
{
    protected final List<Ev<?>> evs;
    protected final Event<?> event;

    public SplitResult(List<Ev<?>> evs, Event<?> event)
    {
        sane(evs, "evs", event, "event");
        this.evs = evs;
        this.event = event;
    }

    public List<Ev<?>> evs()
    {
        return evs;
    }

    public Event<?> event()
    {
        return event;
    }
}
