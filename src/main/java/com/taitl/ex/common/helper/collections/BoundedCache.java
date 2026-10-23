package com.taitl.ex.common.helper.collections;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * A capacity-limited cache for reusable values whose hits avoid the insertion lock.
 * Entries leave in insertion order, so repeated reads do not maintain an access-order list.
 *
 * Resolution runs outside the insertion lock; concurrent misses may compute equivalent values,
 * but publication returns the winning resident value. Resolver failures are not cached.
 * Values must therefore be safe to share and duplicate computation must have no external effect.
 * Clearing releases resident references without cancelling an in-flight resolver; owners must
 * coordinate shutdown before clearing a cache that must remain empty.
 */
public class BoundedCache<K, V>
{
    protected Map<K, V> entries = new ConcurrentHashMap<>();
    protected Queue<K> order = new ArrayDeque<>();
    protected int capacity;

    public BoundedCache(int capacity)
    {
        check(capacity > 0, "Cache capacity must be positive");
        this.capacity = capacity;
    }

    public V get(K key)
    {
        return entries.get(key);
    }

    public V resolve(K key, Supplier<V> resolver)
    {
        V result = entries.get(key);
        if (result != null)
        {
            return result;
        }
        V resolved = resolver.get();
        synchronized (this)
        {
            result = entries.get(key);
            if (result == null)
            {
                put(key, resolved);
                result = resolved;
            }
        }
        return result;
    }

    public synchronized void put(K key, V value)
    {
        if (entries.put(key, value) == null)
        {
            order.add(key);
        }
        while (entries.size() > capacity)
        {
            entries.remove(order.remove());
        }
    }

    public int size()
    {
        return entries.size();
    }

    public synchronized Collection<V> values()
    {
        return new ArrayList<>(entries.values());
    }

    public synchronized void clear()
    {
        entries.clear();
        order.clear();
    }
}
