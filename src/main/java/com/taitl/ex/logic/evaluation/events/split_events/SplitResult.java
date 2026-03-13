package com.taitl.ex.logic.evaluation.events.split_events;

import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Results of SplitEvent.call(): resolved event handlers plus the original event
 * used to derive runtime payload for handler execution.
 * The event handlers are ordered in the order of rules declaration.
 *
 * Example:
 * Main event: RL<Doc<JSON>>
 * Split results:
 * Event: same as main event
 * Evaluables: event handlers for these keys, retrieved
 * from the configuration index (EventField) of this business operation,
 * ordered in the order of rules' declaration.
 *
 * Note that the order of declaration may differ from the above order of the keys.
 * For instance, if a constraint for Read<Doc> is declared before a constraint
 * for Write<Doc<JSON>>, e.g. in a parent context, it will be evaluated first.
 */
public class SplitResult<T>
{
    protected final List<Ev<T>> evaluables;
    protected final Event<T> event;

    public SplitResult(List<Ev<T>> evaluables, Event<T> event)
    {
        sane(evaluables, "evaluables", event, "event");
        this.evaluables = evaluables;
        this.event = event;
    }

    public List<Ev<T>> evaluables()
    {
        return evaluables;
    }

    public Event<T> event()
    {
        return event;
    }
}
