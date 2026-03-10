package com.taitl.ex.common.helper.collections;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Maps key to a list of values.
 * SetList<K, V> == Map<K, List<V>>
 */
public class ListMap<K, V> implements Map<K, List<V>>
{
    public final Supplier<Map<K, List<V>>> DEFAULT_MAP_FACTORY = LinkedHashMap::new;
    public final Supplier<List<V>> DEFAULT_LIST_FACTORY = LinkedList::new;

    protected Supplier<Map<K, List<V>>> mapFactory = DEFAULT_MAP_FACTORY;
    protected Supplier<List<V>> listFactory = DEFAULT_LIST_FACTORY;

    protected Map<K, List<V>> storage;
    protected int size = 0;

    public ListMap()
    {
        this.storage = mapFactory.get();
    }

    public ListMap(Supplier<Map<K, List<V>>> mapFactory, Supplier<List<V>> listFactory)
    {
        this.mapFactory = mapFactory;
        this.listFactory = listFactory;
        this.storage = mapFactory.get();
    }

    /**
     * Gets an element of multimap, in the form of List<V>
     *
     * @return Null if value not present in map
     */
    @Override
    public List<V> get(Object key)
    {
        sane(key, "key");
        List<V> result = storage.get(key);
        return result == null ? null : Collections.unmodifiableList(result);
    }

    public List<V> add(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        synchronized (this)
        {
            List<V> values = storage.computeIfAbsent(key, k -> listFactory.get());
            if (values.isEmpty())
            {
                size++;
                validateSize();
            }
            values.add(value);
            return Collections.unmodifiableList(values);
        }
    }

    @Override
    public List<V> put(K key, List<V> list)
    {
        sane(key, "key");
        sane(list, "list");
        synchronized (this)
        {
            List<V> copy = listFactory.get();
            copy.addAll(list);
            List<V> previous = copy.isEmpty() ? storage.remove(key) : storage.put(key, copy);
            size = storage.size();
            validateSize();
            return previous == null ? null : Collections.unmodifiableList(previous);
        }
    }

    public List<V> putList(K key, List<V> list)
    {
        return put(key, list);
    }

    public V removeValue(Object key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        synchronized (this)
        {
            List<V> values = storage.get(key);
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
    public List<V> remove(Object key)
    {
        sane(key, "key");
        synchronized (this)
        {
            List<V> removed = storage.remove(key);
            if (removed != null)
            {
                size--;
                validateSize();
            }
            return removed == null ? null : Collections.unmodifiableList(removed);
        }
    }

    public List<V> removeMatching(Object key, Predicate<? super V> match)
    {
        sane(key, "key");
        sane(match, "match");
        synchronized (this)
        {
            List<V> values = storage.get(key);
            if (values == null)
            {
                return null;
            }
            List<V> removed = Coll.removeMatching(values, match, listFactory);
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
        sane(key, "key");
        return storage.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        sane(value, "value");
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
    public void putAll(Map<? extends K, ? extends List<V>> m)
    {
        sane(m, "map");
        synchronized (this)
        {
            for (Entry<? extends K, ? extends List<V>> entry : m.entrySet())
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
    public Collection<List<V>> values()
    {
        List<List<V>> wrapped = new ArrayList<>();
        for (List<V> list : storage.values())
        {
            wrapped.add(Collections.unmodifiableList(list));
        }
        return Collections.unmodifiableList(wrapped);
    }

    @Override
    public Set<Entry<K, List<V>>> entrySet()
    {
        Set<Entry<K, List<V>>> wrapped = new LinkedHashSet<>();
        for (Entry<K, List<V>> entry : storage.entrySet())
        {
            wrapped.add(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(),
                    Collections.unmodifiableList(entry.getValue())));
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
            validateSize();
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
