package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Splits non-trivial event, such as 'CU', into a set of elementary events, such as 'Create', 'Update'.
 * Rationale: When looking up the rules defined for an event, to be able to also find the rules
 * defined for any elementary events that comprise it.
 *
 * Examples:
 * <pre>
 * CUD -> CUD, CU, Create, Update, Delete
 * CU -> CU, Create, Update
 * Mutate -> Mutate, Update
 * Transit -> Transit, Mutate, Create, Update, Delete, CU, UD, CUD
 * ReadAndLock -> ReadAndLock, Read
 * </pre>
 */
public class SplitByEventType
{
    @SuppressWarnings("unchecked")
    public <T> Set<Event<T>> call(Event<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        if (event instanceof Port<?>)
        {
            return splitTransit((Port<T>) event, events);
        }
        check(!(event instanceof Mutate<?>), "Please specify event of type Transit<>");
        // TODO: Mutate
        if (event instanceof ReadAndLock<?>)
        {
            return splitReadAndLock((ReadAndLock<T>) event, events);
        }
        // TODO: other

        return events;
    }

    protected <T> Set<Event<T>> splitTransit(Port<T> port, Set<Event<T>> events)
    {
        sane(port, "event", events, "events");
        // Transit -> EntityEvent, Mutate, Transit
        if (port.t0 != null && port.t1 != null)
        {
            events.add(new Mutate<>(port.t0, port.t1));
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
            // events.add(new Write<>(transit.t1));
        }
        // Update
        if (port.t0 != null && port.t1 != null)
        {
            events.add(new Change<>(port.t1));
            events.add(new Update<>(port.t1));
            events.add(new CU<>(port.t1));
            events.add(new UD<>(port.t1));
            events.add(new CUD<>(port.t1));
            // events.add(new Write<>(transit.t1));
        }
        // Delete
        if (port.t1 == null)
        {
            events.add(new Change<>(port.t0));
            events.add(new Delete<>(port.t0));
            events.add(new UD<>(port.t0));
            events.add(new CUD<>(port.t0));
            // events.add(new Write<>(transit.t0));
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
