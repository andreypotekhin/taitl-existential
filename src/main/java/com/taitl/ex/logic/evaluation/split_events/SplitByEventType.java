package com.taitl.ex.logic.evaluation.split_events;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class SplitByEventType
{
    @SuppressWarnings("unchecked")
    public <T> Set<Event<T>> call(Event<T> event, Set<Event<T>> events)
    {
        sane(event, "event", events, "events");
        if (event instanceof Transit<?>)
        {
            return splitTransit((Transit<T>) event, events);
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

    protected <T> Set<Event<T>> splitTransit(Transit<T> transit, Set<Event<T>> events)
    {
        sane(transit, "event", events, "events");
        // Transit -> EntityEvent, Mutate, Transit
        if (transit.t0 != null && transit.t1 != null)
        {
            events.add(new Mutate<>(transit.t0, transit.t1));
        }
        if (transit.t1 != null)
        {
            events.add(new EntityEvent<>(transit.t1));
        }
        // Create
        if (transit.t0 == null)
        {
            events.add(new Create<>(transit.t1));
            events.add(new CU<>(transit.t1));
            events.add(new CUD<>(transit.t1));
            events.add(new Write<>(transit.t1));
        }
        // Update
        if (transit.t0 != null && transit.t1 != null)
        {
            events.add(new Change<>(transit.t1));
            events.add(new Update<>(transit.t1));
            events.add(new CU<>(transit.t1));
            events.add(new UD<>(transit.t1));
            events.add(new CUD<>(transit.t1));
            events.add(new Write<>(transit.t1));
        }
        // Delete
        if (transit.t1 == null)
        {
            events.add(new Change<>(transit.t0));
            events.add(new Delete<>(transit.t0));
            events.add(new UD<>(transit.t0));
            events.add(new CUD<>(transit.t0));
            events.add(new Write<>(transit.t0));
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
