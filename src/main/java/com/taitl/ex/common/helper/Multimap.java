package com.taitl.ex.common.helper;

import java.util.*;
import java.util.function.*;

import static com.taitl.existential.constants.Strings.*;
import static java.util.stream.Collectors.*;

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
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        return storage.get(key);
    }

    public Set<V> put(K key, V value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (value == null)
        {
            throw new IllegalArgumentException(ARG_VALUE);
        }
        synchronized (this)
        {
            Set<V> set = storage.computeIfAbsent(key, k -> new LinkedHashSet<>());
            if (set.isEmpty())
            {
                size++;
                validateSize();
            }
            set.add(value);
            return set;
        }
    }

    public V remove(Object key, V value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (value == null)
        {
            throw new IllegalArgumentException(ARG_VALUE);
        }
        synchronized (this)
        {
            Set<V> set = storage.get(key);
            if (set == null)
            {
                return null;
            }
            boolean removed = set.remove(value);
            if (removed && set.isEmpty())
            {
                size--;
                validateSize();
            }
            return removed ? value : null;
        }
    }

    public Set<V> remove(Object key, Predicate<? super V> match)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (match == null)
        {
            throw new IllegalArgumentException(ARG_MATCH);
        }
        synchronized (this)
        {
            Set<V> set = storage.get(key);
            if (set == null)
            {
                return null;
            }
            Set<V> removed = set.stream().filter(match).collect(toSet());
            set.removeIf(match);
            if (!removed.isEmpty() && set.isEmpty())
            {
                size--;
                validateSize();
            }
            return !removed.isEmpty() ? removed : null;
        }
    }

    public boolean containsKey(K key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        Set<V> values = storage.get(key);
        return values != null && !values.isEmpty();
    }

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
        K result = storage.keySet().stream().findFirst().get();
        return (Class<? extends K>) result.getClass();
    }
}
