package com.taitl.exlogic.events.split;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.naming.Context;

import com.taitl.existential.constants.Strings;
import com.taitl.existential.events.types.BiEvent;
import com.taitl.existential.events.types.EntityEvent;
import com.taitl.existential.events.types.Event;
import com.taitl.existential.events.Change;
import com.taitl.existential.events.Create;
import com.taitl.existential.events.Delete;
import com.taitl.existential.events.Mutate;
import com.taitl.existential.events.Read;
import com.taitl.existential.events.ReadAndLock;
import com.taitl.existential.events.Transit;
import com.taitl.existential.events.Update;
import com.taitl.existential.events.Upsert;
import com.taitl.existential.events.Write;
import com.taitl.existential.transactions.Transaction;

/**
 * Splits events, such as object mutations/transitions, into event sets so that various aspects
 * of an event may be processed/handled separately.
 * <p>
 * For example, splits single transition {@code Transit<House>} into the following event set:<br>
 * <pre>{@code
 *   On<House>
 *   Mutate<House>
 *   Transit<House>
 * }
 * </pre><p>
 * Further, depending on type of transition (Create, Update, Delete), emits the following events :<br>
 *   Created: {@code Create<House>, Write<House>, Upsert<House> }<br>
 *   Updated: {@code Update<House>, Write<House>, Upsert<House>, Change<House>, Mutate<House> }<br>
 *   Deleted: {@code Delete<House>, Write<House>, Change<House>,  }<br>
 * <p>
 * Execution order<br>
 *   Q: In what order are these events created? This is important, since event handlers
 *      create side effects.<br>
 *   A: All effort is made for event handler execution order to not depend on event split creation order.
 *      Execution order of event handlers follows the order in which event handles were
 *      defined in the code. For example:<br>
 *      <pre>{@code
 *        new OnWrite<Cat>(c -> call1()); // A
 *        new OnUpdate<Cat>(c -> call2() ); // B
 *        new OnUpsert<Cat>(c -> call1() ); // C
 *      }</pre>
 *      Execution order of above handlers will be same as their occurrence in the code (A, B, C).<br>
 *      The event handlers defined in the parent context are always executed before the ones from the child context.<br>
 * <p>
 * Customizing<br>
 *   EventSplitter can be customized per context.
 *   <pre>{@code
 *   // Create custom event splitter class
 *   class CustomEventSplitter extends EventSplitter {...}
 *   // Install custom event splitter into a Context
 *   context.splitterFactory(() -> new CustomEventSplitter());
 *   }</pre>
 * <p>
 * @author Andrey Potekhin
 *
 * @see Context
 * @see Transaction
 * @see Event
 * @see EntityEvent
 * @see BiEvent
 * @see Change
 * @see Update
 * @see Upsert
 * @see Delete
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 *
 * TODO Split to stream instead of set
 */
public class EventSplitter
{
    public <T> Set<Event<T>> split(Event<T> event)
    {
        if (event == null)
        {
            throw new IllegalArgumentException(Strings.ARG_EVENT);
        }
        Set<Event<T>> set = new LinkedHashSet<>();
        set.add(event);
        if (event instanceof Transit)
        {
            splitTransit((Transit<T>) event, set);
        }
        else if (event instanceof Mutate)
        {
            throw new IllegalArgumentException(Strings.ARG_NEED_TRANSIT_EVENT);
        }
        // TODO: Mutate
        // TODO: ReadAndLock
        // TODO: other

        return set;
    }

    protected <T> void splitTransit(Transit<T> transit, Set<Event<T>> set)
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
            set.add(new Upsert<>(transit.t1));
            set.add(new Write<>(transit.t1));
        }
        // Update
        if (transit.t0 != null && transit.t1 != null)
        {
            set.add(new Change<>(transit.t0));
            set.add(new Update<>(transit.t1));
            set.add(new Upsert<>(transit.t1));
            set.add(new Write<>(transit.t1));
        }
        // Delete
        if (transit.t1 == null)
        {
            set.add(new Change<>(transit.t0));
            set.add(new Delete<>(transit.t0));
            set.add(new Write<>(transit.t0));
        }
    }

    protected <T> void splitReadAndLock(ReadAndLock<T> event, Set<Event<T>> set)
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
    }
}
