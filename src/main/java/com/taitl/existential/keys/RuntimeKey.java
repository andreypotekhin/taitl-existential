package com.taitl.existential.keys;

import com.taitl.existential.events.types.*;

import static com.taitl.ex.common.helper.State.*;

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
    protected EventKey key;
    protected T entity;

    public static <T> RuntimeKey<T> valueOf(T t)
    {
        return new RuntimeKey<>(t);
    }

    public static <T> RuntimeKey<T> valueOf(Event<T> e, TypeKey<T> typeKey, T t)
    {
        return new RuntimeKey<>(e, typeKey, t);
    }

    public static <T> RuntimeKey<T> valueOf(Event<T> e, String type, T t)
    {
        return new RuntimeKey<>(e, type, t);
    }

    public static <T> RuntimeKey<T> valueOf(Class<T> clz, String type, T t)
    {
        return new RuntimeKey<>(clz, type, t);
    }

    public RuntimeKey(T t)
    {
        key = EventKey.valueOf(t);
        entity = t;
    }

    public RuntimeKey(Event<T> e, TypeKey<T> typeKey, T t)
    {
        key = EventKey.valueOf(e, typeKey);
        entity = t;
    }

    public RuntimeKey(Event<T> e, String type, T t)
    {
        key = EventKey.valueOf(e, type);
        entity = t;
    }

    public RuntimeKey(Class<T> clz, String type, T t)
    {
        key = EventKey.valueOf(clz, type);
        entity = t;
    }

    public int hashCode()
    {
        return key.hashCode() + entity.hashCode();
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
