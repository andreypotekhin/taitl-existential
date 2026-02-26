package com.taitl.ex.logic.evaluation.split_events;

import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Result SplitEvent.call() - the keys that were split off main event,
 * and event handlers for the rules that correspond to the keys.
 * The event handlers are ordered in the order of rules declaration.
 *
 * Example:
 * Main event: ReadAndLock<Doc<JSON>>
 * Split results:
 * Keys: "ReadAndLock<Doc<JSON>>", "ReadAndLock<Doc<?>>", "ReadAndLock<Doc>",
 * "Read<Doc<JSON>>", "Read<Doc<?>>", "Read<Doc>"
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
    protected final Set<RuntimeKey<?>> keys;
    protected final List<Ev<?>> evs;

    public SplitResult(Set<RuntimeKey<?>> keys, List<Ev<?>> evs)
    {
        sane(keys, "splitKeys", evs, "evs");
        this.keys = keys;
        this.evs = evs;
    }

    public Set<RuntimeKey<?>> keys()
    {
        return keys;
    }

    public List<Ev<?>> evs()
    {
        return evs;
    }
}
