package com.taitl.existential.events.types;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Declares the event class a handler responds to,
 * Examples: Create.class, Update.class, Delete.class
 * This small wrapper avoids scattering raw event-class handling across indexing code.
 */
public class EventType
{
    protected final Class<?> eventClass;

    /**
     * Creates an EventType for the provided event class.
     *
     * @param eventClass Event class that implements {@link Event}
     * @return New EventType instance
     */
    public static EventType valueOf(Class<?> eventClass)
    {
        return new EventType(eventClass);
    }

    /**
     * Creates an EventType for the provided event class.
     *
     * @param eventClass Event class that implements {@link Event}
     */
    public EventType(Class<?> eventClass)
    {
        sane(eventClass, "eventClass");
        check(Event.class.isAssignableFrom(eventClass),
                "Argument 'eventClass' should implement Event, got: " + eventClass.getName());
        this.eventClass = eventClass;
    }

    /**
     * Returns the event class for this type.
     *
     * @return Event class
     */
    public Class<?> eventClass()
    {
        return eventClass;
    }

    public boolean biEvent()
    {
        return eventClass.equals(com.taitl.existential.events.Transit.class)
                || eventClass.equals(com.taitl.existential.events.Port.class);
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
