package com.taitl.existential.keys;

import com.taitl.existential.events.types.Event;

import static com.taitl.ex.common.helper.State.verify;

/**
 * Combines an event key (event type + type key) with Object identity for execution of configured rules.
 *
 * Examples: {@code Create<Doc<Json>>+object ref}, {@code Update<Measurement<Float>>+object ref}.
 *
 * @see TypeKey
 * @see Event
 */
public class RuntimeKey<T>
{
    protected final EventKey key;
    protected T entity;

    public static <T> RuntimeKey<T> valueOf(T t)
    {
        return new RuntimeKey<>(t);
    }

    public static <T> RuntimeKey<T> valueOfFull(T t)
    {
        return new RuntimeKey<>(t, true);
    }

    public static <T> RuntimeKey<T> valueOf(Event<T> e, TypeKey<T> typeKey, T t)
    {
        return new RuntimeKey<>(e, typeKey, t);
    }

    public static <T> RuntimeKey<T> valueOfFull(Event<T> e, TypeKey<T> typeKey, T t)
    {
        return new RuntimeKey<>(e, typeKey, t, true);
    }

    public static <T> RuntimeKey<T> valueOf(Event<T> e, String type, T t)
    {
        return new RuntimeKey<>(e, type, t);
    }

    public static <T> RuntimeKey<T> valueOfFull(Event<T> e, String type, T t)
    {
        return new RuntimeKey<>(e, type, t, true);
    }

    public static <T> RuntimeKey<T> valueOf(Class<T> clz, String type, T t)
    {
        return new RuntimeKey<>(clz, type, t);
    }

    public static <T> RuntimeKey<T> valueOfFull(Class<T> clz, String type, T t)
    {
        return new RuntimeKey<>(clz, type, t, true);
    }

    public RuntimeKey(T t)
    {
        this(t, false);
    }

    public RuntimeKey(T t, boolean useFullName)
    {
        this(EventKey.valueOf(t, useFullName), t);
    }

    public RuntimeKey(Event<T> e, TypeKey<T> typeKey, T t)
    {
        this(e, typeKey, t, false);
    }

    public RuntimeKey(Event<T> e, TypeKey<T> typeKey, T t, boolean useFullName)
    {
        this(EventKey.valueOf(e, typeKey, useFullName), t);
    }

    public RuntimeKey(Event<T> e, String type, T t)
    {
        this(e, type, t, false);
    }

    public RuntimeKey(Event<T> e, String type, T t, boolean useFullName)
    {
        this(EventKey.valueOf(e, type, useFullName), t);
    }

    public RuntimeKey(Class<T> clz, String type, T t)
    {
        this(clz, type, t, false);
    }

    public RuntimeKey(Class<T> clz, String type, T t, boolean useFullName)
    {
        this(EventKey.valueOf(clz, type, useFullName), t);
    }

    protected RuntimeKey(EventKey key, T entity)
    {
        this.key = key;
        this.entity = entity;
    }

    public int hashCode()
    {
        int result = (key == null) ? 0 : key.hashCode();
        result = 31 * result + System.identityHashCode(entity);
        return result;
    }

    @SuppressWarnings("unchecked")
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
        if (!(other instanceof RuntimeKey))
        {
            return false;
        }
        RuntimeKey<T> o = (RuntimeKey<T>) other;
        if (o.key == null)
        {
            return (this.key == null);
        }
        boolean sameKey = o.key.equals(this.key);
        boolean sameObject = (o.entity == this.entity);
        return sameKey && sameObject;
    }

    public String toString()
    {
        return key.toString() + "+" + entity;
    }

    public void validate()
    {
        verify(key != null, "RuntimeKey cannot should not be null");
        verify(entity != null, "RuntimeKey entity should not be empty");
    }
}
