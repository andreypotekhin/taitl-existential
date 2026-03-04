package com.taitl.ex.common.helper.collections;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Thread-safe multimap variant.
 * Returned sets are snapshots to avoid exposing mutable internal state across threads.
 */
public class ConcurrentSetMap<K, V> extends SetMap<K, V>
{
    @Override
    public Set<V> get(Object key)
    {
        requireKey(key);
        synchronized (this)
        {
            Set<V> result = storage.get(key);
            return result == null ? null : snapshot(result);
        }
    }

    public Set<V> add(K key, V value)
    {
        requireKey(key);
        requireValue(value);
        synchronized (this)
        {
            Set<V> set = storage.computeIfAbsent(key, k -> setFactory.get());
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
    public Set<V> remove(Object key)
    {
        requireKey(key);
        synchronized (this)
        {
            Set<V> removed = super.remove(key);
            return removed == null ? null : snapshot(removed);
        }
    }

    public V removeValue(Object key, V value)
    {
        requireKey(key);
        requireValue(value);
        synchronized (this)
        {
            return super.removeValue(key, value);
        }
    }

    public Set<V> removeMatching(Object key, Predicate<? super V> match)
    {
        requireKey(key);
        requireMatch(match);
        synchronized (this)
        {
            Set<V> removed = super.removeMatching(key, match);
            return removed == null ? null : snapshot(removed);
        }
    }

    @Override
    public boolean containsKey(Object key)
    {
        requireKey(key);
        synchronized (this)
        {
            return storage.containsKey(key);
        }
    }

    public int size()
    {
        synchronized (this)
        {
            validateSize();
            return size;
        }
    }

    public void clear()
    {
        synchronized (this)
        {
            storage.clear();
            size = 0;
        }
    }

    @SuppressWarnings("unchecked")
    public Class<? extends K> getKeyClass()
    {
        synchronized (this)
        {
            return MapKeys.keyClass(storage, size);
        }
    }

    protected Set<V> snapshot(Set<V> values)
    {
        return Collections.unmodifiableSet(new LinkedHashSet<>(values));
    }
}
