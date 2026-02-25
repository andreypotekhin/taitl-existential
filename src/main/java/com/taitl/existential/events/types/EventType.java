package com.taitl.existential.events.types;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Declares the event class a handler responds to (for example {@code Create.class}).
 * This small wrapper avoids scattering raw event-class handling across indexing code.
 */
public class EventType
{
    protected final Class<?> eventClass;

    public static EventType valueOf(Class<?> eventClass)
    {
        return new EventType(eventClass);
    }

    public EventType(Class<?> eventClass)
    {
        sane(eventClass, "eventClass");
        check(Event.class.isAssignableFrom(eventClass),
                "Argument 'eventClass' should implement Event, got: " + eventClass.getName());
        this.eventClass = eventClass;
    }

    public Class<?> eventClass()
    {
        return eventClass;
    }

    public int hashCode()
    {
        return eventClass.hashCode();
    }

    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (!(other instanceof EventType))
        {
            return false;
        }
        return eventClass.equals(((EventType) other).eventClass);
    }

    public String toString()
    {
        return eventClass.getSimpleName();
    }
}
