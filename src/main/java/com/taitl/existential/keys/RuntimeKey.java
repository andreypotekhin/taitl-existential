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
    protected final TypeKey<T> typeKey;
    protected final Event<T> event;
    protected T entity;

    public static <T> RuntimeKey<T> valueOf(T t, boolean useFullName)
    {
        return new RuntimeKey<>(t, useFullName);
    }

    public static <T> RuntimeKey<T> valueOf(Event<T> e, TypeKey<T> typeKey, T t, boolean useFullName)
    {
        return new RuntimeKey<>(e, typeKey, t, useFullName);
    }

    public static <T> RuntimeKey<T> valueOf(Event<T> e, String type, T t, boolean useFullName)
    {
        return new RuntimeKey<>(e, type, t, useFullName);
    }

    public static <T> RuntimeKey<T> valueOf(Class<T> clz, String type, T t, boolean useFullName)
    {
        return new RuntimeKey<>(clz, type, t, useFullName);
    }

    public RuntimeKey(T t)
    {
        this(t, false);
    }

    public RuntimeKey(T t, boolean useFullName)
    {
        this(EventKey.valueOf(t, useFullName), TypeKey.valueOf(t, useFullName), null, t);
    }

    public RuntimeKey(Event<T> e, TypeKey<T> typeKey, T t)
    {
        this(e, typeKey, t, false);
    }

    public RuntimeKey(Event<T> e, TypeKey<T> typeKey, T t, boolean useFullName)
    {
        this(EventKey.valueOf(e, typeKey, useFullName), typeKey, e, t);
    }

    public RuntimeKey(Event<T> e, String type, T t)
    {
        this(e, type, t, false);
    }

    public RuntimeKey(Event<T> e, String type, T t, boolean useFullName)
    {
        this(EventKey.valueOf(e, type, useFullName), TypeKey.valueOf(type), e, t);
    }

    public RuntimeKey(Class<T> clz, String type, T t)
    {
        this(clz, type, t, false);
    }

    public RuntimeKey(Class<T> clz, String type, T t, boolean useFullName)
    {
        this(EventKey.valueOf(clz, type, useFullName), TypeKey.valueOf(type), null, t);
    }

    protected RuntimeKey(EventKey key, T entity)
    {
        this(key, key != null ? (TypeKey<T>) key.typeKey() : null, null, entity);
    }

    protected RuntimeKey(EventKey key, TypeKey<T> typeKey, Event<T> event, T entity)
    {
        this.key = key;
        this.typeKey = typeKey;
        this.event = event;
        this.entity = entity;
    }

    public EventKey key()
    {
        return key;
    }

    public TypeKey<T> typeKey()
    {
        return typeKey;
    }

    public Event<T> event()
    {
        return event;
    }

    public T entity()
    {
        return entity;
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
        verify(typeKey != null, "RuntimeKey typeKey should not be empty");
        verify(entity != null, "RuntimeKey entity should not be empty");
    }
}
