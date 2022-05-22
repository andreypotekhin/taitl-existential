package com.taitl.existential.unused;

import com.taitl.existential.event.types.BiEvent;
import com.taitl.existential.event.types.Event;

/**
 * Implements a key for collections keyed by Event + Type.
 * 
 * The key consists of Event<T> class and Type<T> describing the entity class.
 * 
 * Examples: Doc, Read<Doc>, Create<Doc<Json>>
 * 
 * @author Andrey Potekhin
 * @see Event
 * @see BiEvent
 * @see Type
 */
public class EventAndTypeKey<T>
{
    String key;

    EventAndTypeKey(Event<T> e, Type type)
    {
        String eventClass = e.getClass().getSimpleName();
        if ("Event".equals(eventClass))
        {
            // Generic 'Event' - use class name only, like 'Doc'
            key = type.toString();
        }
        else
        {
            // Use event + class name , like 'Create<Doc<Json>>'
            key = eventClass + "<" + type.toString() + ">";
        }
    }

    EventAndTypeKey(Class<T> clz, Type type)
    {
        String eventClass = clz.getSimpleName();
        if ("Event".equals(eventClass))
        {
            // Generic 'Event' - use class name only, like 'Doc'
            key = type.toString();
        }
        else
        {
            // Use event + class name , like 'Create<Doc<Json>>'
            key = eventClass + "<" + type.toString() + ">";
        }
    }

    EventAndTypeKey(BiEvent<T> e, Type type)
    {
        String eventClass = e.getClass().getSimpleName();
        key = eventClass + "<" + type.toString() + ">";
    }

    // TODO: equals, hashCode
}
