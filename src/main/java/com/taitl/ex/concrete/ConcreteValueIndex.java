package com.taitl.ex.concrete;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Backing implementation for {@link com.taitl.existential.indexes.ValueIndex}.
 */
public class ConcreteValueIndex<K, V> implements Map<K, V>
{
    protected static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#index-key-mismatch";

    protected Map<K, V> storage = new LinkedHashMap<>();
    protected final Function<V, K> getKey;

    public ConcreteValueIndex(Function<V, K> getKey)
    {
        sane(getKey, "getKey");
        this.getKey = getKey;
    }

    public boolean contains(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        V current = storage.get(key);
        return current != null && current.equals(value);
    }

    public boolean contains(K key, Predicate<? super V> match)
    {
        sane(key, "key");
        sane(match, "match");
        V current = storage.get(key);
        return current != null && match.test(current);
    }

    public V add(V value)
    {
        sane(value, "value");
        put(getKey.apply(value), value);
        return value;
    }

    public void addAll(Collection<? extends V> values)
    {
        sane(values, "values");
        for (V value : values)
        {
            add(value);
        }
    }

    public V removeMatching(K key, Predicate<? super V> match)
    {
        sane(key, "key");
        sane(match, "match");
        synchronized (this)
        {
            V current = storage.get(key);
            if (current == null || !match.test(current))
            {
                return null;
            }
            storage.remove(key);
            return current;
        }
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
            if (remove(oldKey, value))
            {
                put(newKey, value);
            }
        }
    }

    public void reindex(K oldKey, V value)
    {
        sane(oldKey, "oldKey");
        sane(value, "value");
        reindex(oldKey, getKey.apply(value), value);
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
            remove(getKey.apply(oldValue), oldValue);
            return;
        }
        sane(oldValue, "oldValue", newValue, "newValue");
        K oldKey = getKey.apply(oldValue);
        K newKey = getKey.apply(newValue);
        synchronized (this)
        {
            remove(oldKey, oldValue);
            put(newKey, newValue);
        }
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
    public V get(Object key)
    {
        sane(key, "key");
        return storage.get(key);
    }

    @Override
    public V put(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        synchronized (this)
        {
            return storage.put(key, value);
        }
    }

    @Override
    public V remove(Object key)
    {
        sane(key, "key");
        synchronized (this)
        {
            return storage.remove(key);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m)
    {
        sane(m, "map");
        synchronized (this)
        {
            for (Entry<? extends K, ? extends V> entry : m.entrySet())
            {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void clear()
    {
        synchronized (this)
        {
            storage.clear();
        }
    }

    @Override
    public Set<K> keySet()
    {
        return Collections.unmodifiableSet(new LinkedHashSet<>(storage.keySet()));
    }

    @Override
    public Collection<V> values()
    {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        Set<Entry<K, V>> wrapped = new LinkedHashSet<>();
        for (Entry<K, V> entry : storage.entrySet())
        {
            wrapped.add(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), entry.getValue()));
        }
        return Collections.unmodifiableSet(wrapped);
    }
}
