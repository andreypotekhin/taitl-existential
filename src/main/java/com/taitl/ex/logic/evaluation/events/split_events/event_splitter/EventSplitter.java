package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.ex.common.creator.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Splits compound events, such as Mutation, CUD, into set of elementary events for individual handling.
 * 
 * For example, splits a single transition {@code Transit<House>} into the following event set:
 * <pre>{@code
 *   Mutate<House>
 *   Transit<House>
 *   On<House>
 * }
 * </pre>
 * Further, depending on type of transition (Create, Update, Delete), emits the following events:
 *   Created: {@code Create<House>, CU<House>, CUD<House> }
 *   Updated: {@code Update<House>, CU<House>, UD<House>, CUD<House>, Change<House>, Mutate<House> }
 *   Deleted: {@code Delete<House>, UD<House>, CUD<House> }
 * 
 * Execution order
 *   Q: In what order are these events created? This is important, since event handlers
 *      create side effects.
 *   A: All effort is made for event handler execution order to not depend on event split creation order.
 *      Execution order of event handlers follows the order in which event handles were
 *      defined in the code. For example:
 *      <pre>{@code
 *        new OnWrite<Cat>(c -> call1()); // A
 *        new OnUpdate<Cat>(c -> call2() ); // B
 *        new OnCU<Cat>(c -> call1() ); // C
 *      }</pre>
 *      Execution order of above handlers will be same as their occurrence in the code (A, B, C).
 *      The event handlers defined in the parent context are always executed before the ones from the child context.
 */
public class EventSplitter
{
    public static Supplier<? extends EventSplitter> FACTORY = () -> Creator.create(EventSplitter.class);
    protected SplitTypeKey splitTypeKey = Creator.create(SplitTypeKey.class);
    protected SplitByEventType splitByEventType = Creator.create(SplitByEventType.class);

    public <T> Set<RuntimeKey<T>> split(RuntimeKey<T> runtimeKey)
    {
        return split(runtimeKey, false);
    }

    public <T> Set<RuntimeKey<T>> split(RuntimeKey<T> runtimeKey, boolean useFullEventNames)
    {
        sane(runtimeKey, "runtimeKey");
        Event<T> event = runtimeKey.event();
        sane(event, "event");
        Set<Event<T>> events = splitEvent(event);
        Set<TypeKey<T>> typeKeys = splitTypeKey.split(runtimeKey.typeKey());
        Set<RuntimeKey<T>> runtimeKeys = new LinkedHashSet<>();
        for (Event<T> splitEvent : events)
        {
            for (TypeKey<T> typeKey : typeKeys)
            {
                runtimeKeys.add(new RuntimeKey<>(splitEvent, typeKey, runtimeEntity(splitEvent, runtimeKey),
                        useFullEventNames));
            }
        }
        return runtimeKeys;
    }

    public <T> Set<Event<T>> splitEvent(Event<T> event)
    {
        sane(event, "event");
        Set<Event<T>> events = new LinkedHashSet<>();
        events.add(event);
        return splitByEventType.call(event, events);
    }

    @SuppressWarnings("unchecked")
    protected <T> T runtimeEntity(Event<T> event, RuntimeKey<T> runtimeKey)
    {
        if (event instanceof BiEvent<?>)
        {
            BiEvent<T> biEvent = (BiEvent<T>) event;
            return biEvent.t1 != null ? biEvent.t1 : biEvent.t0;
        }
        if (event instanceof EntityEvent<?>)
        {
            return ((EntityEvent<T>) event).t;
        }
        return runtimeKey.entity();
    }

    protected <T> Set<Event<T>> splitTransit(Port<T> port, Set<Event<T>> events)
    {
        return splitByEventType.splitTransit(port, events);
    }

    protected <T> Set<Event<T>> splitReadAndLock(ReadAndLock<T> event, Set<Event<T>> events)
    {
        return splitByEventType.splitReadAndLock(event, events);
    }
}
