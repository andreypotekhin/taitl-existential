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

    public static <T> EventKey valueOfFull(T t)
    {
        return new EventKey(t, true);
    }

    public static <T> EventKey valueOf(Event<T> e, TypeKey<T> typeKey)
    {
        return new EventKey(e, typeKey);
    }

    public static <T> EventKey valueOfFull(Event<T> e, TypeKey<T> typeKey)
    {
        return new EventKey(e, typeKey, true);
    }

    public static <T> EventKey valueOf(Event<T> e, String type)
    {
        return new EventKey(e, type);
    }

    public static <T> EventKey valueOfFull(Event<T> e, String type)
    {
        return new EventKey(e, type, true);
    }

    public static <T> EventKey valueOf(Class<T> clz, String type)
    {
        return new EventKey(clz, type);
    }

    public static <T> EventKey valueOfFull(Class<T> clz, String type)
    {
        return new EventKey(clz, type, true);
    }

    public EventKey(String s)
    {
        key = s;
    }

    public <T> EventKey(T t)
    {
        this(t, false);
    }

    public <T> EventKey(T t, boolean useFullName)
    {
        key = useFullName ? t.getClass().getName() : t.getClass().getSimpleName();
    }

    public <T> EventKey(Event<T> e, TypeKey<T> typeKey)
    {
        this(e, typeKey, false);
    }

    public <T> EventKey(Event<T> e, TypeKey<T> typeKey, boolean useFullName)
    {
        String eventClass = useFullName ? e.getClass().getName() : e.getClass().getSimpleName();
        // Use event + class name , like 'Create<Doc<JSON>>'
        key = eventClass + "<" + typeKey.toString() + ">";
    }

    public <T> EventKey(Event<T> e, String type)
    {
        this(e, type, false);
    }

    public <T> EventKey(Event<T> e, String type, boolean useFullName)
    {
        String eventClass = useFullName ? e.getClass().getName() : e.getClass().getSimpleName();
        // Use event + class name , like 'Create<Doc<JSON>>'
        key = eventClass + "<" + type + ">";
    }

    public <T> EventKey(Class<T> clz, String type)
    {
        this(clz, type, false);
    }

    public <T> EventKey(Class<T> clz, String type, boolean useFullName)
    {
        String eventClass = useFullName ? clz.getName() : clz.getSimpleName();
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
