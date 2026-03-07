package com.taitl.ex.concrete;

import com.taitl.ex.common.helper.collections.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Backing implementation for {@link com.taitl.existential.indexes.SetIndex}.
 */
public class ConcreteSetIndex<K, V> implements Map<K, Set<V>>
{
    protected static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#index-key-mismatch";

    protected SetMap<K, V> storage = new SetMap<>();
    protected final Function<V, K> getKey;

    public ConcreteSetIndex(Function<V, K> getKey)
    {
        sane(getKey, "getKey");
        this.getKey = getKey;
    }

    /**
     * Indexes value updates with null-tolerant semantics.
     * If oldValue is null, add newValue.
     * If newValue is null, remove oldValue.
     * Otherwise move from old key to new key.
     */
    public void index(V oldValue, V newValue)
    {
        if (oldValue == null)
        {
            add(newValue);
            return;
        }
        if (newValue == null)
        {
            removeValue(getKey.apply(oldValue), oldValue);
            return;
        }
        sane(oldValue, "oldValue", newValue, "newValue");
        K oldKey = getKey.apply(oldValue);
        K newKey = getKey.apply(newValue);
        synchronized (this)
        {
            removeValue(oldKey, oldValue);
            add(newKey, newValue);
        }
    }

    @Override
    public Set<V> get(Object key)
    {
        sane(key, "key");
        return storage.get(key);
    }

    public boolean contains(V value)
    {
        sane(value, "value");
        K key = getKey.apply(value);
        sane(key, "key");
        Set<V> set = storage.get(key);
        if (set == null || set.isEmpty())
        {
            return false;
        }
        return set.contains(value);
    }

    public boolean contains(K key, Predicate<Set<V>> match)
    {
        sane(key, "key");
        sane(match, "match");
        Set<V> set = storage.get(key);
        if (set == null || set.isEmpty())
        {
            return false;
        }
        return match.test(set);
    }

    public Set<V> add(V value)
    {
        sane(value, "value");
        return storage.add(getKey.apply(value), value);
    }

    public Set<V> add(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        return storage.add(key, value);
    }

    public void addAll(Collection<? extends V> values)
    {
        sane(values, "values");
        for (V value : values)
        {
            add(value);
        }
    }

    public V removeValue(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        return storage.removeValue(key, value);
    }

    public Set<V> remove(K key, Predicate<? super V> match)
    {
        sane(key, "key");
        sane(match, "match");
        return storage.removeMatching(key, match);
    }

    public void reindex(K oldKey, K newKey, V value)
    {
        sane(oldKey, "oldKey");
        sane(newKey, "newKey");
        sane(value, "value");
        K key = getKey.apply(value);
        if (!newKey.equals(key))
        {
            throw new IllegalArgumentException(String.format(
                    "Argument 'newKey' value '%s' does not match key value '%s'"
                            + " returned by 'getKey' function. See " + TROUBLESHOOTING_SECTION,
                    newKey, key));
        }
        synchronized (this)
        {
            removeValue(oldKey, value);
            add(newKey, value);
        }
    }

    public void reindex(K oldKey, V value)
    {
        sane(oldKey, "oldKey");
        sane(value, "value");
        reindex(oldKey, getKey.apply(value), value);
    }

    @Override
    public int size()
    {
        return storage.size();
    }

    @Override
    public boolean isEmpty()
    {
        return storage.isEmpty();
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

    @Override
    public Set<V> put(K key, Set<V> value)
    {
        sane(key, "key");
        sane(value, "value");
        return storage.put(key, value);
    }

    @Override
    public Set<V> remove(Object key)
    {
        sane(key, "key");
        return storage.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Set<V>> m)
    {
        sane(m, "map");
        storage.putAll(m);
    }

    @Override
    public void clear()
    {
        storage.clear();
    }

    @Override
    public Set<K> keySet()
    {
        return storage.keySet();
    }

    @Override
    public Collection<Set<V>> values()
    {
        return storage.values();
    }

    @Override
    public Set<Entry<K, Set<V>>> entrySet()
    {
        return storage.entrySet();
    }
}
