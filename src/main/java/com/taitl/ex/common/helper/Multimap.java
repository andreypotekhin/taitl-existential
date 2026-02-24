package com.taitl.ex.common.helper;

import java.util.*;
import java.util.function.*;

import static com.taitl.existential.constants.Strings.*;

/**
 * Maps key to a set of values.
 * Multimap<K, V> == Map<K, Set<V>>
 */
public class Multimap<K, V>
{
    protected Map<K, Set<V>> storage = new LinkedHashMap<>();
    protected int size = 0;

    /**
     * Gets an element of multimap, in the form of Set<V>
     * 
     * @return Null or empty set if value not present in map
     */
    public Set<V> get(K key)
    {
        requireKey(key);
        Set<V> result = storage.get(key);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    public Set<V> put(K key, V value)
    {
        requireKey(key);
        requireValue(value);
        synchronized (this)
        {
            Set<V> values = storage.computeIfAbsent(key, k -> new LinkedHashSet<>());
            if (values.isEmpty())
            {
                size++;
                validateSize();
            }
            values.add(value);
            return Collections.unmodifiableSet(values);
        }
    }

    public V remove(Object key, V value)
    {
        requireKey(key);
        requireValue(value);
        synchronized (this)
        {
            Set<V> values = storage.get(key);
            if (values == null)
            {
                return null;
            }
            boolean removed = values.remove(value);
            if (removed && values.isEmpty())
            {
                size--;
                validateSize();
            }
            return removed ? value : null;
        }
    }

    public Set<V> remove(Object key, Predicate<? super V> match)
    {
        requireKey(key);
        requireMatch(match);
        synchronized (this)
        {
            Set<V> values = storage.get(key);
            if (values == null)
            {
                return null;
            }
            Set<V> removed = new LinkedHashSet<>();
            Iterator<V> iterator = values.iterator();
            while (iterator.hasNext())
            {
                V value = iterator.next();
                if (match.test(value))
                {
                    removed.add(value);
                    iterator.remove();
                }
            }
            if (!removed.isEmpty() && values.isEmpty())
            {
                size--;
                validateSize();
            }
            return !removed.isEmpty() ? removed : null;
        }
    }

    public boolean containsKey(K key)
    {
        requireKey(key);
        Set<V> values = storage.get(key);
        return values != null && !values.isEmpty();
    }

    /**
     * Returns the number of keys that currently hold at least one value.
     */
    public int size()
    {
        validateSize();
        return size;
    }

    public void clear()
    {
        synchronized (this)
        {
            storage.clear();
            size = 0;
        }
    }

    protected void validateSize()
    {
        if (size < 0)
        {
            throw new IllegalStateException("Failure detected: size less than zero.");
        }
        if (size > storage.size())
        {
            throw new IllegalStateException("Failure detected: size greater than storage size.");
        }
    }

    @SuppressWarnings("unchecked")
    public Class<? extends K> getKeyClass()
    {
        if (size == 0)
        {
            throw new IllegalStateException("You can't call method getKeyClass() on an empty Multimap.");
        }
        K result = Coll.getFirst(storage.keySet());
        return (Class<? extends K>) result.getClass();
    }

    protected void requireKey(Object key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
    }

    protected void requireValue(Object value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(ARG_VALUE);
        }
    }

    protected void requireMatch(Object match)
    {
        if (match == null)
        {
            throw new IllegalArgumentException(ARG_MATCH);
        }
    }
}
