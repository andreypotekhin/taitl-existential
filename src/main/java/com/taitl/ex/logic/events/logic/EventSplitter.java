package com.taitl.ex.logic.events.logic;

import javax.naming.Context;
import java.util.*;
import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;

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
 * Customizing
 *   EventSplitter can be customized per context.
 *   <pre>{@code
 *   // Create custom EventSplitter class
 *   class CustomEventSplitter extends EventSplitter {...}
 *   // Install custom event splitter into a Context
 *   context.eventSplitterFactory(() -> new CustomEventSplitter());
 *   }</pre>
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
 *
 * TODO Add the ability to return set of applicable EventKey's:
 * e.g. for "ReadAndLock<Doc<JSON>>" also return "ReadAndLock<Doc>", "Read<Doc<JSON>>", "Read<Doc>"
 */
public class EventSplitter
{
    public static Supplier<? extends EventSplitter> FACTORY =
            () -> Creator.create(EventSplitter.class);

    public <T> Set<Event<T>> split(Event<T> event)
    {
        sane(event, "event");
        Set<Event<T>> set = new LinkedHashSet<>();
        set.add(event);
        if (event instanceof Transit)
        {
            return splitTransit((Transit<T>) event, set);
        }
        check(!(event instanceof Mutate), "Please specify event of type Transit<>");
        // TODO: Mutate
        if (event instanceof ReadAndLock)
        {
            return splitReadAndLock((ReadAndLock<T>) event, set);
        }
        // TODO: other

        return set;
    }

    protected <T> Set<Event<T>> splitTransit(Transit<T> transit, Set<Event<T>> set)
    {
        if (transit == null)
        {
            throw new IllegalArgumentException(Strings.ARG_PERM_KEY);
        }
        if (set == null)
        {
            throw new IllegalArgumentException(Strings.ARG_SET);
        }
        // Transit -> EntityEvent, Mutate, Transit
        if (transit.t0 != null && transit.t1 != null)
        {
            set.add(new Mutate<>(transit.t0, transit.t1));
        }
        if (transit.t1 != null)
        {
            set.add(new EntityEvent<>(transit.t1));
        }
        // Create
        if (transit.t0 == null)
        {
            set.add(new Create<>(transit.t1));
            set.add(new CU<>(transit.t1));
            set.add(new Write<>(transit.t1));
        }
        // Update
        if (transit.t0 != null && transit.t1 != null)
        {
            set.add(new Change<>(transit.t0));
            set.add(new Update<>(transit.t1));
            set.add(new CU<>(transit.t1));
            set.add(new Write<>(transit.t1));
        }
        // Delete
        if (transit.t1 == null)
        {
            set.add(new Change<>(transit.t0));
            set.add(new Delete<>(transit.t0));
            set.add(new Write<>(transit.t0));
        }
        return set;
    }

    protected <T> Set<Event<T>> splitReadAndLock(ReadAndLock<T> event, Set<Event<T>> set)
    {
        if (event == null)
        {
            throw new IllegalArgumentException(Strings.ARG_EVENT);
        }
        if (set == null)
        {
            throw new IllegalArgumentException(Strings.ARG_SET);
        }
        set.add(new Read<>(event.t));
        return set;
    }
}
