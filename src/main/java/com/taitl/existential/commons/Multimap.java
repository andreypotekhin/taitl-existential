package com.taitl.existential.commons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Multimap<K, V>
{
    public static final String KEY_ARG = "Argument 'key' must not be null.";
    public static final String VALUE_ARG = "Argument 'value' must not be null.";

    Map<K, Set<V>> storage = new HashMap<>();
    int size = 0;

    /**
     * Gets an element of multimap, in the form of Set<V>
     * 
     * @return Null or empty set if value not present in map
     */
    public Set<V> get(K key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(KEY_ARG);
        }
        return storage.get(key);
    }

    public Set<V> put(K key, V value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(KEY_ARG);
        }
        if (value == null)
        {
            throw new IllegalArgumentException(VALUE_ARG);
        }
        Set<V> set = storage.computeIfAbsent(key, k -> new HashSet<>());
        if (set.isEmpty())
        {
            size++;
            validateSize();
        }
        set.add(value);
        return set;
    }

    public V remove(Object key, V value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(KEY_ARG);
        }
        if (value == null)
        {
            throw new IllegalArgumentException(VALUE_ARG);
        }
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

    public boolean containsKey(K key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(KEY_ARG);
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
        storage.clear();
        size = 0;
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
}
