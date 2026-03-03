package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Splits compound event, such as 'CU', into a set of elementary events, such as 'Create', 'Update',
 * to cover the rules which could be defined for any of the elementary events.
 * Splits elementary event, such as 'Create' to matching compound events (e.g. 'CU'),
 * to cover the rules which could be defined for any of the compound events that correspond to elementary event.
 * Rationale: When looking up the rules defined for an event, to be able to also find the rules
 * defined for any elementary/compound events that match the original event.
 *
 * Examples:
 * <pre>
 * 1. Compound event to elementary events
 * CUD -> CUD, CU, Create, Update, Delete, Transit, Port
 * CU -> CU, Create, Update, Transit, Port
 * UD -> UD, Update, Delete, Transit, Port
 * Transit -> Transit, Update
 * Port -> Port, Transit, Create, Update, Delete, CU, UD, CUD
 * ReadAndLock -> ReadAndLock, Read
 *
 * 2. Elementary event to matching compound events if not already encountered in step 1
 * Create -> Create, CUD, CU, Port
 * Update -> Update, CUD, CU, Transit
 * Delete -> Delete, CUD, UD, Port
 * </pre>
 */
public class SplitEventType
{
    @SuppressWarnings("unchecked")
    public <T> Set<Event<T>> call(Event<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        if (event instanceof Port<?>)
        {
            return splitPort((Port<T>) event, events);
        }
        check(!(event instanceof Transit<?>), "Please specify event of type Port<>");
        // TODO: Transit
        if (event instanceof ReadAndLock<?>)
        {
            return splitReadAndLock((ReadAndLock<T>) event, events);
        }
        // TODO: other

        return events;
    }

    protected <T> Set<Event<T>> splitPort(Port<T> port, Set<Event<T>> events)
    {
        sane(port, "event", events, "events");
        // Port -> EntityEvent, Transit, Port
        if (port.t0 != null && port.t1 != null)
        {
            events.add(new Transit<>(port.t0, port.t1));
        }
        if (port.t1 != null)
        {
            events.add(new EntityEvent<>(port.t1));
        }
        // Create
        if (port.t0 == null)
        {
            events.add(new Create<>(port.t1));
            events.add(new CU<>(port.t1));
            events.add(new CUD<>(port.t1));
        }
        // Update
        if (port.t0 != null && port.t1 != null)
        {
            events.add(new Update<>(port.t1));
            events.add(new CU<>(port.t1));
            events.add(new UD<>(port.t1));
            events.add(new CUD<>(port.t1));
        }
        // Delete
        if (port.t1 == null)
        {
            events.add(new Delete<>(port.t0));
            events.add(new UD<>(port.t0));
            events.add(new CUD<>(port.t0));
        }
        return events;
    }

    protected <T> Set<Event<T>> splitReadAndLock(ReadAndLock<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        events.add(new Read<>(event.t));
        return events;
    }
}
