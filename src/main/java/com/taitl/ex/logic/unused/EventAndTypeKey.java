package com.taitl.ex.logic.unused;

import com.taitl.existential.events.types.*;
import java.util.Objects;

/**
 * Implements a key for collections keyed by Event + Type.
 * 
 * The key consists of Event<T> class and Type<T> describing the entity class.
 * 
 * Examples: Doc, Read<Doc>, Create<Doc<Json>>
 * 
 * @see Event
 * @see BiEvent
 * @see Type
 * @deprecated Use EventKey
 */
@Deprecated
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

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof EventAndTypeKey))
        {
            return false;
        }
        EventAndTypeKey<?> that = (EventAndTypeKey<?>) other;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(key);
    }
}
