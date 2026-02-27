package com.taitl.ex.logic.evaluation.split_events;

import com.taitl.ex.common.creator.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Splits events, such as object mutations/transitions, into event sets so that various aspects
 * of an event may be processed/handled separately.
 * 
 * For example, splits single transition {@code Transit<House>} into the following event set:
 * <pre>{@code
 *   On<House>
 *   Mutate<House>
 *   Transit<House>
 * }
 * </pre>
 * Further, depending on type of transition (Create, Update, Delete), emits the following events:
 *   Created: {@code Create<House>, Write<House>, Upsert<House> }
 *   Updated: {@code Update<House>, Write<House>, Upsert<House>, Change<House>, Mutate<House> }
 *   Deleted: {@code Delete<House>, Write<House>, Change<House>,  }
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
 *        new OnUpsert<Cat>(c -> call1() ); // C
 *      }</pre>
 *      Execution order of above handlers will be same as their occurrence in the code (A, B, C).
 *      The event handlers defined in the parent context are always executed before the ones from the child context.
 * 
 * @see Context
 * @see Transaction
 * @see Event
 * @see EntityEvent
 * @see BiEvent
 * @see Change
 * @see Update
 * @see CU
 * @see Delete
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
public class EventSplitter
{
    public static Supplier<? extends EventSplitter> FACTORY =
            () -> Creator.create(EventSplitter.class);
    protected final SplitTypeKey splitTypeKey = new SplitTypeKey();
    protected SplitByEventType splitByEventType = new SplitByEventType();

    public <T> Set<RuntimeKey<T>> split(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        Event<T> event = runtimeKey.event();
        check(event != null, "RuntimeKey event should not be null");
        Set<Event<T>> events = splitEvent(event);
        Set<TypeKey<T>> typeKeys = splitTypeKey.split(runtimeKey.typeKey());
        Set<RuntimeKey<T>> runtimeKeys = new LinkedHashSet<>();
        for (Event<T> splitEvent : events)
        {
            for (TypeKey<T> typeKey : typeKeys)
            {
                runtimeKeys.add(new RuntimeKey<>(splitEvent, typeKey, runtimeEntity(splitEvent, runtimeKey), false));
            }
        }
        return runtimeKeys;
    }

    protected <T> Set<Event<T>> splitEvent(Event<T> event)
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
            return ((BiEvent<T>) event).t1;
        }
        if (event instanceof EntityEvent<?>)
        {
            return ((EntityEvent<T>) event).t;
        }
        return runtimeKey.entity();
    }

    protected <T> Set<Event<T>> splitTransit(Transit<T> transit, Set<Event<T>> events)
    {
        return splitByEventType.splitTransit(transit, events);
    }

    protected <T> Set<Event<T>> splitReadAndLock(ReadAndLock<T> event, Set<Event<T>> events)
    {
        return splitByEventType.splitReadAndLock(event, events);
    }
}
