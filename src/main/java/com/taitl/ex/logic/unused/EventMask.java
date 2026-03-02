package com.taitl.ex.logic.unused;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Thread-safe registry of event type bit positions.
 */
@Deprecated
public class EventMask
{
    protected static Map<String, Integer> eventBits = new ConcurrentHashMap<>();
    protected static Map<Integer, String> eventTypes = new ConcurrentHashMap<>();
    protected static volatile int maxEventBit = -1;

    static
    {
        registerEventType(new TypeKey<Event>() {
        });
        registerEventType(new TypeKey<Create>() {
        });
        registerEventType(new TypeKey<Update>() {
        });
        registerEventType(new TypeKey<CU>() {
        });
        registerEventType(new TypeKey<Delete>() {
        });
        registerEventType(new TypeKey<Read>() {
        });
        registerEventType(new TypeKey<BiEvent>() {
        });
        registerEventType(new TypeKey<Mutate>() {
        });
        registerEventType(new TypeKey<Port>() {
        });
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
    public static int getEventBit(TypeKey et)
    {
        Integer result = eventBits.get(et.toString());
        if (result == null)
        {
            throw new IllegalStateException(String.format("Event bit not defined for event '%s'", et));
        }
        return result;
    }

    public static TypeKey getEventType(int bit)
    {
        String result = eventTypes.get(bit);
        if (result == null)
        {
            throw new IllegalStateException(String.format("Event bit '%d' not assigned to any event type", bit));
        }
        return TypeKey.valueOf(result);
    }

    /**
     * Returns the max number of all event bits that can be returned by getEventBit().
     * This allows to set the size of bit mask which would cover all known events.
     *
     * @return Maximum number of all event bits that can be returned by getEventBit()
     */
    public static int getMaxEventBit()
    {
        return maxEventBit;
    }

    /**
     * Registers a new event type in EventMask, making it possible to start using
     * the new event type in event handlers.
     * <p>
     * Example:<br>
     * <pre>{@code
     *   // Define custom event type
     *   class Receive<T> extends EntityEvent<T> ...
     *   // Register custom event type
     *   EventMask.registerEventType(TypeKey.valueOf(Receive.class, false))
     *   // Declare custom event handler class
     *   class OnReceive<T> extends On<T>  ...
     *   // Use custom event in custom event handler
     *   Ex.contexts().get("/myapp/mymodule")
     *     .handle(new OnReceive<Email>(e -> Slack.post(channel, e)));
     * }</pre>
     *
     * @param <T> Custom event type to register
     * @param type Typekey describing the new event, for instance, TypeKey.valueOf(MyEvent.class, false)
     * @return The bit number for the newly registered event type
     */
    public static synchronized <T extends Event<?>> int registerEventType(TypeKey<T> type)
    {
        if (type == null)
        {
            throw new IllegalArgumentException("Argument 'type' should not be null");
        }
        String typeString = type.toString();
        if (eventBits.containsKey(typeString))
        {
            throw new IllegalArgumentException(String.format("Event bit already assigned to class '%s'", type));
        }
        Integer bitKey = eventBits.size();
        eventBits.put(typeString, bitKey);
        maxEventBit = bitKey;
        if (eventTypes.containsKey(bitKey))
        {
            throw new IllegalArgumentException(String.format("Event type already assigned to bit '%d'", bitKey));
        }
        eventTypes.put(bitKey, typeString);
        return bitKey;
    }
}
