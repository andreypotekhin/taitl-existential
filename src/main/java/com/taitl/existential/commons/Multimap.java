package com.taitl.existential.commons;

import static com.taitl.existential.constants.Strings.KEY_ARG;
import static com.taitl.existential.constants.Strings.VALUE_ARG;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Multimap<K, V>
{
    Map<K, Set<V>> storage = new LinkedHashMap<>();
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
            throw new IllegalArgumentException(KEY_ARG);
        }
        if (value == null)
        {
            throw new IllegalArgumentException(VALUE_ARG);
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
}
