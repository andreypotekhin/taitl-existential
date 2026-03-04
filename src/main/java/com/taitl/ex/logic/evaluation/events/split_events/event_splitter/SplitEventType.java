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
    public <T> Set<Event<T>> call(Event<T> event, Set<Event<T>> events)
    {
        return call(event, events, true);
    }

    @SuppressWarnings("unchecked")
    public <T> Set<Event<T>> call(Event<T> event, Set<Event<T>> events, boolean splitElementaryToCompound)
    {
        sane(event, "event", events, "events");
        splitCompoundRoot(event, events);
        if (splitElementaryToCompound)
        {
            splitElementaryRoot(event, events);
        }
        return events;
    }

    @SuppressWarnings("unchecked")
    protected <T> Set<Event<T>> splitCompoundRoot(Event<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        if (event instanceof Port<?>)
        {
            return splitPort((Port<T>) event, events);
        }
        if (event instanceof Transit<?>)
        {
            return splitTransit((Transit<T>) event, events);
        }
        if (event instanceof ReadAndLock<?>)
        {
            return splitReadAndLock((ReadAndLock<T>) event, events);
        }
        if (event instanceof CUD<?>)
        {
            return splitCud((CUD<T>) event, events);
        }
        if (event instanceof CU<?>)
        {
            return splitCu((CU<T>) event, events);
        }
        if (event instanceof UD<?>)
        {
            return splitUd((UD<T>) event, events);
        }
        return events;
    }

    @SuppressWarnings("unchecked")
    protected <T> Set<Event<T>> splitElementaryRoot(Event<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        if (event instanceof Create<?>)
        {
            return splitCreate((Create<T>) event, events);
        }
        if (event instanceof Update<?>)
        {
            return splitUpdate((Update<T>) event, events);
        }
        if (event instanceof Delete<?>)
        {
            return splitDelete((Delete<T>) event, events);
        }
        return events;
    }

    protected <T> Set<Event<T>> splitTransit(Transit<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        events.add(new Update<>(event.t1));
        return events;
    }

    protected <T> Set<Event<T>> splitPort(Port<T> port, Set<Event<T>> events)
    {
        sane(port, "event", events, "events");
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

    protected <T> Set<Event<T>> splitCud(CUD<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        T entity = event.t;
        events.add(new CU<>(entity));
        events.add(new Create<>(entity));
        events.add(new Update<>(entity));
        events.add(new Delete<>(entity));
        events.add(new Transit<>(entity, entity));
        events.add(new Port<>(entity, entity));
        return events;
    }

    protected <T> Set<Event<T>> splitCu(CU<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        T entity = event.t;
        events.add(new Create<>(entity));
        events.add(new Update<>(entity));
        events.add(new Transit<>(entity, entity));
        events.add(new Port<>(entity, entity));
        return events;
    }

    protected <T> Set<Event<T>> splitUd(UD<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        T entity = event.t;
        events.add(new Update<>(entity));
        events.add(new Delete<>(entity));
        events.add(new Transit<>(entity, entity));
        events.add(new Port<>(entity, entity));
        return events;
    }

    protected <T> Set<Event<T>> splitCreate(Create<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        T entity = event.t;
        events.add(new CUD<>(entity));
        events.add(new CU<>(entity));
        events.add(new Port<>(entity, entity));
        return events;
    }

    protected <T> Set<Event<T>> splitUpdate(Update<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        T entity = event.t;
        events.add(new CUD<>(entity));
        events.add(new CU<>(entity));
        events.add(new Transit<>(entity, entity));
        return events;
    }

    protected <T> Set<Event<T>> splitDelete(Delete<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        T entity = event.t;
        events.add(new CUD<>(entity));
        events.add(new UD<>(entity));
        events.add(new Port<>(entity, entity));
        return events;
    }

    protected <T> Set<Event<T>> splitReadAndLock(ReadAndLock<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        events.add(new Read<>(event.t));
        return events;
    }
}
