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

    public static <T> EventKey valueOf(T t)
    {
        return new EventKey(t);
    }

    public static <T> EventKey valueOf(Event<T> e, TypeKey<T> typeKey)
    {
        return new EventKey(e, typeKey);
    }

    public static <T> EventKey valueOf(Event<T> e, String type)
    {
        return new EventKey(e, type);
    }

    public static <T> EventKey valueOf(Class<T> clz, String type)
    {
        return new EventKey(clz, type);
    }

    public EventKey(String s)
    {
        key = s;
    }

    public <T> EventKey(T t)
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
        if (!(other instanceof TypeKey))
        {
            return false;
        }
        TypeKey o = (TypeKey) other;
        if (o.typeid == null)
        {
            return (this.key == null);
        }
        return o.typeid.equals(this.key);
    }

    public String toString()
    {
        return key;
    }
}
