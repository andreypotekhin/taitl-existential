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
    public Set<V> get(K key)
    {
        requireKey(key);
        synchronized (this)
        {
            Set<V> result = storage.get(key);
            return result == null ? null : snapshot(result);
        }
    }

    public Set<V> put(K key, V value)
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

    public V remove(Object key, V value)
    {
        requireKey(key);
        requireValue(value);
        synchronized (this)
        {
            return super.remove(key, value);
        }
    }

    public Set<V> remove(Object key, Predicate<? super V> match)
    {
        requireKey(key);
        requireMatch(match);
        synchronized (this)
        {
            Set<V> removed = super.remove(key, match);
            return removed == null ? null : snapshot(removed);
        }
    }

    public boolean containsKey(K key)
    {
        requireKey(key);
        synchronized (this)
        {
            Set<V> values = storage.get(key);
            return values != null && !values.isEmpty();
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
            if (size == 0)
            {
                throw new IllegalStateException("You can't call method getKeyClass() on an empty Multimap.");
            }
            K result = Coll.getFirst(storage.keySet());
            return (Class<? extends K>) result.getClass();
        }
    }

    protected Set<V> snapshot(Set<V> values)
    {
        return Collections.unmodifiableSet(new LinkedHashSet<>(values));
    }
}
