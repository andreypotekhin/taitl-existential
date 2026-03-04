package com.taitl.ex.common.helper.collections;

import java.util.*;
import java.util.function.*;

/**
 * Maps key to a set of values.
 * SetMap<K, V> == Map<K, Set<V>>
 */
public class SetMap<K, V> implements Map<K, Set<V>>
{
    public final Supplier<Map<K, Set<V>>> DEFAULT_MAP_FACTORY = LinkedHashMap::new;
    public final Supplier<Set<V>> DEFAULT_SET_FACTORY = LinkedHashSet::new;

    protected Supplier<Map<K, Set<V>>> mapFactory = DEFAULT_MAP_FACTORY;
    protected Supplier<Set<V>> setFactory = DEFAULT_SET_FACTORY;

    protected Map<K, Set<V>> storage;
    protected int size = 0;

    public SetMap()
    {
        this.storage = mapFactory.get();
    }

    public SetMap(Supplier<Map<K, Set<V>>> mapFactory, Supplier<Set<V>> setFactory)
    {
        this.mapFactory = mapFactory;
        this.setFactory = setFactory;
        this.storage = mapFactory.get();
    }

    /**
     * Gets an element of multimap, in the form of Set<V>
     *
     * @return Null if value not present in map
     */
    @Override
    public Set<V> get(Object key)
    {
        requireKey(key);
        Set<V> result = storage.get(key);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    public Set<V> add(K key, V value)
    {
        requireKey(key);
        requireValue(value);
        synchronized (this)
        {
            Set<V> values = storage.computeIfAbsent(key, k -> setFactory.get());
            if (values.isEmpty())
            {
                size++;
                validateSize();
            }
            values.add(value);
            return Collections.unmodifiableSet(values);
        }
    }

    @Override
    public Set<V> put(K key, Set<V> values)
    {
        requireKey(key);
        requireValue(values);
        synchronized (this)
        {
            Set<V> copy = setFactory.get();
            copy.addAll(values);
            Set<V> previous = storage.put(key, copy);
            size = storage.size();
            validateSize();
            return previous == null ? null : Collections.unmodifiableSet(previous);
        }
    }

    public V removeValue(Object key, V value)
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
                storage.remove(key);
                size--;
                validateSize();
            }
            return removed ? value : null;
        }
    }

    @Override
    public Set<V> remove(Object key)
    {
        requireKey(key);
        synchronized (this)
        {
            Set<V> removed = storage.remove(key);
            if (removed != null)
            {
                size--;
                validateSize();
            }
            return removed == null ? null : Collections.unmodifiableSet(removed);
        }
    }

    public Set<V> removeMatching(Object key, Predicate<? super V> match)
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
            Set<V> removed = Coll.removeMatching(values, match, setFactory);
            if (!removed.isEmpty() && values.isEmpty())
            {
                storage.remove(key);
                size--;
                validateSize();
            }
            return !removed.isEmpty() ? removed : null;
        }
    }

    @Override
    public boolean containsKey(Object key)
    {
        requireKey(key);
        return storage.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        requireValue(value);
        return storage.containsValue(value);
    }

    /**
     * Returns the number of keys that currently hold at least one value.
     */
    @Override
    public int size()
    {
        validateSize();
        return size;
    }

    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Set<V>> m)
    {
        requireValue(m);
        synchronized (this)
        {
            for (Entry<? extends K, ? extends Set<V>> entry : m.entrySet())
            {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Set<K> keySet()
    {
        return Collections.unmodifiableSet(storage.keySet());
    }

    @Override
    public Collection<Set<V>> values()
    {
        List<Set<V>> wrapped = new ArrayList<>();
        for (Set<V> set : storage.values())
        {
            wrapped.add(Collections.unmodifiableSet(set));
        }
        return Collections.unmodifiableList(wrapped);
    }

    @Override
    public Set<Entry<K, Set<V>>> entrySet()
    {
        Set<Entry<K, Set<V>>> wrapped = new LinkedHashSet<>();
        for (Entry<K, Set<V>> entry : storage.entrySet())
        {
            wrapped.add(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(),
                    Collections.unmodifiableSet(entry.getValue())));
        }
        return Collections.unmodifiableSet(wrapped);
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
        return MapKeys.keyClass(storage, size);
    }

    protected void requireKey(Object key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Argument 'key' must not be null");
        }
    }

    protected void requireValue(Object value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("Argument 'value' must not be null");
        }
    }

    protected void requireMatch(Object match)
    {
        if (match == null)
        {
            throw new IllegalArgumentException("Argument 'match' must not be null");
        }
    }
}
