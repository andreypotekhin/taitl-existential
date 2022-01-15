package com.taitl.existential;

import com.taitl.existential.events.BiEvent;
import com.taitl.existential.events.Event;

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
public class EventKey<T>
{
    String key;

    EventKey(Event<T> e, Type type)
    {
        String eventClass = e.getClass().getSimpleName();
        if ("Event".equals(eventClass))
        {
            // Generic 'Event' - use class name only, like 'Doc'
            key = type.getTypeid();
        }
        else
        {
            // Use event + class name , like 'Create<Doc<Json>>'
            key = eventClass + "<" + type.getTypeid() + ">";
        }
    }

    EventKey(Class<T> clz, Type type)
    {
        String eventClass = clz.getSimpleName();
        if ("Event".equals(eventClass))
        {
            // Generic 'Event' - use class name only, like 'Doc'
            key = type.getTypeid();
        }
        else
        {
            // Use event + class name , like 'Create<Doc<Json>>'
            key = eventClass + "<" + type.getTypeid() + ">";
        }
    }

    EventKey(BiEvent<T> e, Type type)
    {
        String eventClass = e.getClass().getSimpleName();
        key = eventClass + "<" + type.getTypeid() + ">";
    }
}
