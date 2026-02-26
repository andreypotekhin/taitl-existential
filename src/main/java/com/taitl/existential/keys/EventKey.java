package com.taitl.existential.keys;

import com.taitl.existential.events.types.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Combines an event type with a {@link TypeKey} to address configured handlers.
 *
 * Examples: {@code Create<Doc<Json>>}, {@code Update<Measurement<Float>>}.
 *
 * @see TypeKey
 * @see Event
 */
// Todo: introduce type parameter
public class EventKey
{
    protected final String key;
    protected final TypeKey<?> typeKey;

    public static EventKey valueOf(String s)
    {
        return new EventKey(s);
    }

    public static <T> EventKey valueOf(T instance, boolean useFullName)
    {
        return new EventKey(instance, useFullName);
    }

    public static <T> EventKey valueOf(Event<T> event, TypeKey<T> typeKey, boolean useFullName)
    {
        return new EventKey(event, typeKey, useFullName);
    }

    public static <T> EventKey valueOf(Event<T> event, String type, boolean useFullName)
    {
        return new EventKey(event, type, useFullName);
    }

    public static <T> EventKey valueOf(Class<T> eventClass, String type, boolean useFullName)
    {
        return new EventKey(eventClass, type, useFullName);
    }

    public EventKey(String s)
    {
        sane(s, "key");
        this.key = s;
        this.typeKey = typeKeyFromSerializedKey(s);
    }

    public <T> EventKey(T instance)
    {
        this(instance, false);
    }

    public <T> EventKey(T instance, boolean useFullName)
    {
        sane(instance, "instance");
        this.typeKey = TypeKey.valueOf(instance, useFullName);
        String eventClassName = useFullName ? instance.getClass().getName() : instance.getClass().getSimpleName();
        this.key = eventClassName + "<" + typeKey + ">";
    }

    public <T> EventKey(Event<T> event, TypeKey<T> typeKey)
    {
        this(event, typeKey, false);
    }

    public <T> EventKey(Event<T> event, TypeKey<T> typeKey, boolean useFullName)
    {
        sane(event, "event", typeKey, "typeKey");
        this.typeKey = typeKey;
        String eventClass = useFullName ? event.getClass().getName() : event.getClass().getSimpleName();
        // Use event type + type key, like 'Create<Doc<JSON>>'
        this.key = eventClass + "<" + typeKey.toString() + ">";
    }

    public <T> EventKey(Event<T> event, String type)
    {
        this(event, type, false);
    }

    public <T> EventKey(Event<T> event, String type, boolean useFullName)
    {
        sane(event, "event", type, "type");
        this.typeKey = TypeKey.valueOf(type);
        String eventClass = useFullName ? event.getClass().getName() : event.getClass().getSimpleName();
        // Use event type + type name, like 'Create<Doc<JSON>>'
        this.key = eventClass + "<" + type + ">";
    }

    public <T> EventKey(Class<T> eventClass, String type)
    {
        this(eventClass, type, false);
    }

    public EventKey(Class<?> eventClass, TypeKey<?> typeKey)
    {
        this(eventClass, typeKey, false);
    }

    public EventKey(Class<?> eventClass, TypeKey<?> typeKey, boolean useFullName)
    {
        sane(eventClass, "eventClass", typeKey, "typeKey");
        this.typeKey = typeKey;
        String eventClassName = useFullName ? eventClass.getName() : eventClass.getSimpleName();
        this.key = eventClassName + "<" + typeKey + ">";
    }

    public <T> EventKey(Class<T> eventClass, String type, boolean useFullName)
    {
        sane(eventClass, "eventClass", type, "type");
        this.typeKey = TypeKey.valueOf(type);
        String eventClassName = useFullName ? eventClass.getName() : eventClass.getSimpleName();
        // Use event type + type name, like 'Create<Doc<JSON>>'
        this.key = eventClassName + "<" + type + ">";
    }

    public TypeKey<?> typeKey()
    {
        return typeKey;
    }

    protected static TypeKey<?> typeKeyFromSerializedKey(String key)
    {
        int open = key.indexOf('<');
        int close = key.lastIndexOf('>');
        if (open >= 0 && close > open)
        {
            String typePart = key.substring(open + 1, close);
            return TypeKey.valueOf(typePart);
        }
        return TypeKey.valueOf(key);
    }

    public static EventKey valueOf(Class<?> eventClass, TypeKey<?> typeKey)
    {
        return new EventKey(eventClass, typeKey);
    }

    public static EventKey valueOfFull(Class<?> eventClass, TypeKey<?> typeKey)
    {
        return new EventKey(eventClass, typeKey, true);
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
