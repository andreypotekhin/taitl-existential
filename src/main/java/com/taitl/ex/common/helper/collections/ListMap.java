package com.taitl.ex.common.helper.collections;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Maps key to a list of values.
 * SetList<K, V> == Map<K, List<V>>
 */
public class ListMap<K, V>
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
     * @return Null or empty set if value not present in map
     */
    public List<V> get(K key)
    {
        sane(key, "key");
        List<V> result = storage.get(key);
        return result == null ? null : Collections.unmodifiableList(result);
    }

    public List<V> put(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        synchronized (this)
        {
            List<V> values = storage.computeIfAbsent(key, k -> listFactory.get());
            if (values.isEmpty())
            {
                size++;
            }
            values.add(value);
            return Collections.unmodifiableList(values);
        }
    }

    public List<V> putList(K key, List<V> list)
    {
        sane(key, "key");
        sane(list, "list");
        synchronized (this)
        {
            List<V> values = storage.computeIfAbsent(key, k -> listFactory.get());
            if (values.isEmpty())
            {
                size++;
            }
            values.addAll(list);
            return Collections.unmodifiableList(values);
        }
    }

    public V remove(Object key, V value)
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
                size--;
            }
            return removed ? value : null;
        }
    }

    public List<V> remove(Object key, Predicate<? super V> match)
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
                size--;
            }
            return !removed.isEmpty() ? removed : null;
        }
    }

    public boolean containsKey(K key)
    {
        sane(key, "key");
        List<V> values = storage.get(key);
        return values != null && !values.isEmpty();
    }

    /**
     * Returns the number of keys that currently hold at least one value.
     */
    public int size()
    {
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
}
