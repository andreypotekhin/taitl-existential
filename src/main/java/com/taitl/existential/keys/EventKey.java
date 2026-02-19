package com.taitl.existential.keys;

import com.taitl.existential.events.types.*;

/**
 * Combines an event type with a {@link TypeKey} to address configured handlers.
 *
 * Examples: {@code Create<Doc<Json>>}, {@code Update<Measurement<Float>>}.
 *
 * @see TypeKey
 * @see Event
 */
public class EventKey
{
    protected String key;

    public static EventKey valueOf(String s)
    {
        return new EventKey(s);
    }

    public EventKey(String s)
    {
        key = s;
    }

    public EventKey(Object t)
    {
        key = t.getClass().getSimpleName();
    }

    public <T> EventKey(Event<T> e, TypeKey<T> typeKey)
    {
        String eventClass = e.getClass().getSimpleName();
        // Use event + class name , like 'Create<Doc<JSON>>'
        key = eventClass + "<" + typeKey.toString() + ">";
    }

    public <T> EventKey(Event<T> e, String type)
    {
        String eventClass = e.getClass().getSimpleName();
        // Use event + class name , like 'Create<Doc<JSON>>'
        key = eventClass + "<" + type + ">";
    }

    public <T> EventKey(Class<T> clz, String type)
    {
        String eventClass = clz.getSimpleName();
        // Use event + class name , like 'Create<Doc<JSON>>'
        key = eventClass + "<" + type + ">";
    }

    public int hashCode()
    {
        return key.hashCode();
    }

    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        if (!(other instanceof EventKey))
        {
            return false;
        }
        EventKey o = (EventKey) other;
        if (o.key == null)
        {
            return (this.key == null);
        }
        return o.key.equals(this.key);
    }

    public String toString()
    {
        return key;
    }
}
