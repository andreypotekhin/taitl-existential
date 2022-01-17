package com.taitl.existential;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.Context;
import javax.swing.event.DocumentEvent.EventType;

import com.taitl.existential.events.BiEvent;
import com.taitl.existential.events.Create;
import com.taitl.existential.events.Delete;
import com.taitl.existential.events.Event;
import com.taitl.existential.events.Mutate;
import com.taitl.existential.events.Permutate;
import com.taitl.existential.events.Read;
import com.taitl.existential.events.Update;
import com.taitl.existential.events.Upsert;
import com.taitl.existential.transactions.Transaction;

/**
 * Splits events so that various event aspects may be handled separately.
 *  
 * For example, splits one Permutation<House> event into this sequence: 
 *   On<House>
 *   Mutation<House>
 *   Permutation<House>
 *   
 * Moreover, depending on permutation:
 *   Created: Create<House>, Update<House>, Permutate<House>
 *   Updated: Update<House>, Mutate<House>, Permutate<House>
 *   Deleted: Delete<House>, Permutate<House>
 *   
 * Customizing
 *   EventSplitter can be customized per context.
 *   // Create custom event splitter class
 *   class CustomEventSplitter extends EventSplitter...
 *   // Install custom event splitter into a Context
 *   context.eventSplitterFactory(() -> new CustomEventSplitter());
 * 
 * @author Andrey Potekhin
 * @see Context
 * @see Transaction
 *
 */
public class EventSplitter
{
    final int MAX_EVENT_BIT;

    Map<String, Integer> eventBits = new LinkedHashMap<>();

    public EventSplitter()
    {
        eventBits.put(TypeKey.valueOf(Event.class).toString(), Integer.valueOf(0));
        eventBits.put(TypeKey.valueOf(Create.class).toString(), Integer.valueOf(1));
        eventBits.put(TypeKey.valueOf(Update.class).toString(), Integer.valueOf(2));
        eventBits.put(TypeKey.valueOf(Upsert.class).toString(), Integer.valueOf(3));
        eventBits.put(TypeKey.valueOf(Delete.class).toString(), Integer.valueOf(4));
        eventBits.put(TypeKey.valueOf(Read.class).toString(), Integer.valueOf(5));
        eventBits.put(TypeKey.valueOf(BiEvent.class).toString(), Integer.valueOf(10));
        eventBits.put(TypeKey.valueOf(Mutate.class).toString(), Integer.valueOf(11));
        eventBits.put(TypeKey.valueOf(Permutate.class).toString(), Integer.valueOf(12));
        MAX_EVENT_BIT = 12;
    }

    /**
     * Returns the number of bit in bitmask for this event type.
     * The number can be used as index in bitmask.  
     * 
     * Example:
     *   Event -> 0
     *   Create -> 1
     *   Update -> 2
     *   Upsert -> 3
     *   Delete -> 4
     *   Read -> 5
     *   Mutate -> 11
     *   Permutate -> 12
     * 
     * For use in event bit masks, e.g. in Transaction class
     * 
     * @param et EventType, e.g. Create, Update, Delete, Read, Mutate, etc.
     * @return The number representing a bit position for this type in events bitmask
     */
    public int getEventBit(EventType et)
    {
        Integer result = eventBits.get(et.toString());
        if (result == null)
        {
            throw new IllegalStateException(String.format("Event bit not defined for event '%s'.", et));
        }
        return result;
    }

    /**
     * Returns the max number of all event bits that can be returned by getEventBit().
     * This allows to set the size of bit mask which would cover all known events.
     * 
     * @return Maximum number of all event bits that can be returned by getEventBit()
     */
    public int getMaxEventBit()
    {
        return MAX_EVENT_BIT;
    }
}
