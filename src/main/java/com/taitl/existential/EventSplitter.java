package com.taitl.existential;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.naming.Context;

import com.taitl.existential.constants.Strings;
import com.taitl.existential.events.Change;
import com.taitl.existential.events.Create;
import com.taitl.existential.events.Delete;
import com.taitl.existential.events.Mutate;
import com.taitl.existential.events.Permutate;
import com.taitl.existential.events.Read;
import com.taitl.existential.events.ReadAndLock;
import com.taitl.existential.events.Update;
import com.taitl.existential.events.Upsert;
import com.taitl.existential.events.Write;
import com.taitl.existential.events.base.BiEvent;
import com.taitl.existential.events.base.EntityEvent;
import com.taitl.existential.events.base.Event;
import com.taitl.existential.transactions.Transaction;

/**
 * Splits mutations into event sets so that various aspects of an event may be processed/handled separately.
 * <p>
 * For example, splits mutation {@code Permutation<House>} into an event set:<br>
 * {@code
 *   EntityEvent<House>
 *   Permutate<House>
 * }
 * <p>  
 * Moreover, adds following depending on type of permutation (Create, Update, Delete):<br>
 *   Create: {@code Create<House>, Write<House>, Upsert<House> }<br>
 *   Update: {@code Update<House>, Write<House>, Upsert<House>, Change<House>, Mutate<House> }<br>
 *   Delete: {@code Delete<House>, Write<House>, Change<House>,  }<br>
 * <p>
 * Execution order<br>
 *   Q: In what order are these events created? This is important, since event handlers 
 *      create side effects.<br>
 *   A: All effort is made for event handler execution order to not depend on event split creation order.
 *      The event handler execution order follows the order in which event handles are 
 *      defined in the code. For example:<br>
 *      <pre>{@code    
 *        new OnWrite<Cat>(c -> call1()); // A 
 *        new OnUpdate<Cat>(c -> call2() ); // B
 *        new OnUpsert<Cat>(c -> call1() ); // C
 *      }</pre>
 *      The execution order of the above handlers will be same as their occurrence in the code (A, B, C).<br>
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
 * @see Permutate
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
        if (event instanceof Permutate)
        {
            splitPermutate((Permutate<T>) event, set);
        }

        return set;
    }

    protected <T> void splitPermutate(Permutate<T> perm, Set<Event<T>> set)
    {
        if (perm == null)
        {
            throw new IllegalArgumentException(Strings.ARG_PERM_KEY);
        }
        if (set == null)
        {
            throw new IllegalArgumentException(Strings.ARG_SET);
        }
        // Permutate -> EntityEvent, Mutate, Permutate
        if (perm.t0 != null && perm.t1 != null)
        {
            set.add(new Mutate<>(perm.t0, perm.t1));
        }
        if (perm.t1 != null)
        {
            set.add(new EntityEvent<>(perm.t1));
        }
        // Create
        if (perm.t0 == null)
        {
            set.add(new Create<>(perm.t1));
            set.add(new Upsert<>(perm.t1));
            set.add(new Write<>(perm.t1));
        }
        // Update
        if (perm.t0 != null && perm.t1 != null)
        {
            set.add(new Change<>(perm.t0));
            set.add(new Update<>(perm.t1));
            set.add(new Upsert<>(perm.t1));
            set.add(new Write<>(perm.t1));
        }
        // Delete
        if (perm.t1 == null)
        {
            set.add(new Change<>(perm.t0));
            set.add(new Delete<>(perm.t0));
            set.add(new Write<>(perm.t0));
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
