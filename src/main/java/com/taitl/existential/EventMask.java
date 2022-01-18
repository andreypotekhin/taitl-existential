package com.taitl.existential;

import java.util.LinkedHashMap;
import java.util.Map;

import com.taitl.existential.events.BiEvent;
import com.taitl.existential.events.Create;
import com.taitl.existential.events.Delete;
import com.taitl.existential.events.Event;
import com.taitl.existential.events.Mutate;
import com.taitl.existential.events.Permutate;
import com.taitl.existential.events.Read;
import com.taitl.existential.events.Update;
import com.taitl.existential.events.Upsert;

public class EventMask
{
    protected static Map<String, Integer> eventBits = new LinkedHashMap<>();
    protected static Map<Integer, String> eventTypes = new LinkedHashMap<>();
    protected static int maxEventBit = -1;

    static
    {
        assignBitToEventType(Event.class, 0);
        assignBitToEventType(Create.class, 1);
        assignBitToEventType(Update.class, 2);
        assignBitToEventType(Upsert.class, 3);
        assignBitToEventType(Delete.class, 4);
        assignBitToEventType(Read.class, 5);
        assignBitToEventType(BiEvent.class, 10);
        assignBitToEventType(Mutate.class, 11);
        assignBitToEventType(Permutate.class, 12);
    }

    public EventMask()
    {
        assignBitToEventType(Event.class, 0);
        assignBitToEventType(Create.class, 1);
        assignBitToEventType(Update.class, 2);
        assignBitToEventType(Upsert.class, 3);
        assignBitToEventType(Delete.class, 4);
        assignBitToEventType(Read.class, 5);
        assignBitToEventType(BiEvent.class, 10);
        assignBitToEventType(Mutate.class, 11);
        assignBitToEventType(Permutate.class, 12);
    }

    /**
     * Returns the number of bit in bitmask for this event type.
     * The number can be used as index in bitmask.  
     * 
     * Example:
     * <pre>
     *   Event -> 0
     *   Create -> 1
     *   Update -> 2
     *   Upsert -> 3
     *   Delete -> 4
     *   Read -> 5
     *   Mutate -> 11
     *   Permutate -> 12
     * </pre>
     * For use in event bit masks, e.g. in Transaction class
     * 
     * @param et EventKey, e.g. Create, Update, Delete, Read, Mutate, etc.
     * @return The number representing a bit position for this type in events bitmask
     */
    public int getEventBit(EventKey et)
    {
        Integer result = eventBits.get(et.toString());
        if (result == null)
        {
            throw new IllegalStateException(String.format("Event bit not defined for event '%s'", et));
        }
        return result;
    }

    public EventKey getEventType(int bit)
    {
        String result = eventTypes.get(bit);
        if (result == null)
        {
            throw new IllegalStateException(String.format("Event bit '%d' not assigned to any event type", bit));
        }
        return EventKey.valueOf(result);
    }

    /**
     * Returns the max number of all event bits that can be returned by getEventBit().
     * This allows to set the size of bit mask which would cover all known events.
     * 
     * @return Maximum number of all event bits that can be returned by getEventBit()
     */
    public int getMaxEventBit()
    {
        return maxEventBit;
    }

    protected static <T> void assignBitToEventType(Class<T> clz, int bit)
    {
        if (!Event.class.isAssignableFrom(clz))
        {
            throw new IllegalArgumentException("Argument 'clz' must be a subclass of Event");
        }
        String typeKey = TypeKey.valueOf(clz).toString();
        if (eventBits.containsKey(typeKey))
        {
            throw new IllegalArgumentException(String.format("Event bit already assigned to class '%s'", typeKey));
        }
        Integer bitKey = Integer.valueOf(bit);
        if (eventTypes.containsKey(bitKey))
        {
            throw new IllegalArgumentException(String.format("Event bit already assigned to bit '%d'", bitKey));
        }
        synchronized (eventBits)
        {
            eventBits.put(typeKey, bitKey);
            eventTypes.put(bitKey, typeKey);
            if (maxEventBit < bit)
            {
                maxEventBit = bit;
            }
        }
    }
}
