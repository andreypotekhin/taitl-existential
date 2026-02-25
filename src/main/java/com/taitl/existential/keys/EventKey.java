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
public class EventKey
{
    protected final String key;

    public static EventKey valueOf(String s)
    {
        return new EventKey(s);
    }

    public static <T> EventKey valueOf(T instance)
    {
        return new EventKey(instance);
    }

    public static <T> EventKey valueOfFull(T instance)
    {
        return new EventKey(instance, true);
    }

    public static <T> EventKey valueOf(Event<T> event, TypeKey<T> typeKey)
    {
        return new EventKey(event, typeKey);
    }

    public static <T> EventKey valueOfFull(Event<T> event, TypeKey<T> typeKey)
    {
        return new EventKey(event, typeKey, true);
    }

    public static <T> EventKey valueOf(Event<T> event, String type)
    {
        return new EventKey(event, type);
    }

    public static <T> EventKey valueOfFull(Event<T> event, String type)
    {
        return new EventKey(event, type, true);
    }

    public static <T> EventKey valueOf(Class<T> eventClass, String type)
    {
        return new EventKey(eventClass, type);
    }

    public static <T> EventKey valueOfFull(Class<T> eventClass, String type)
    {
        return new EventKey(eventClass, type, true);
    }

    public EventKey(String s)
    {
        sane(s, "key");
        this.key = s;
    }

    public <T> EventKey(T instance)
    {
        this(instance, false);
    }

    public <T> EventKey(T instance, boolean useFullName)
    {
        sane(instance, "instance");
        this.key = useFullName ? instance.getClass().getName() : instance.getClass().getSimpleName();
    }

    public <T> EventKey(Event<T> event, TypeKey<T> typeKey)
    {
        this(event, typeKey, false);
    }

    public <T> EventKey(Event<T> event, TypeKey<T> typeKey, boolean useFullName)
    {
        sane(event, "event", typeKey, "typeKey");
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
        String eventClassName = useFullName ? eventClass.getName() : eventClass.getSimpleName();
        this.key = eventClassName + "<" + typeKey + ">";
    }

    public <T> EventKey(Class<T> eventClass, String type, boolean useFullName)
    {
        sane(eventClass, "eventClass", type, "type");
        String eventClassName = useFullName ? eventClass.getName() : eventClass.getSimpleName();
        // Use event type + type name, like 'Create<Doc<JSON>>'
        this.key = eventClassName + "<" + type + ">";
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
