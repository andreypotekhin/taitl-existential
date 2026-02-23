package com.taitl.ex.common.helper;

import java.util.*;
import java.util.function.*;

import static com.taitl.existential.constants.Strings.*;

/**
 * Thread-safe multimap variant.
 * Returned sets are snapshots to avoid exposing mutable internal state across threads.
 */
public class ConcurrentMultimap<K, V> extends Multimap<K, V>
{
    @Override
    public Set<V> get(K key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        synchronized (this)
        {
            Set<V> result = storage.get(key);
            return result == null ? null : snapshot(result);
        }
    }

    @Override
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
            return snapshot(set);
        }
    }

    @Override
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
            return super.remove(key, value);
        }
    }

    @Override
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
            Set<V> removed = super.remove(key, match);
            return removed == null ? null : snapshot(removed);
        }
    }

    @Override
    public boolean containsKey(K key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        synchronized (this)
        {
            Set<V> values = storage.get(key);
            return values != null && !values.isEmpty();
        }
    }

    @Override
    public int size()
    {
        synchronized (this)
        {
            validateSize();
            return size;
        }
    }

    @Override
    public void clear()
    {
        synchronized (this)
        {
            storage.clear();
            size = 0;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends K> getKeyClass()
    {
        synchronized (this)
        {
            if (size == 0)
            {
                throw new IllegalStateException("You can't call method getKeyClass() on an empty Multimap.");
            }
            K result = storage.keySet().stream().findFirst().get();
            return (Class<? extends K>) result.getClass();
        }
    }

    protected Set<V> snapshot(Set<V> values)
    {
        return Collections.unmodifiableSet(new LinkedHashSet<>(values));
    }
}
