package com.taitl.existential.indexes;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Maps a key (K) to a single value (V).
 * Note: null is not allowed as a key or as a value.
 */
public class ValueIndex<K, V> implements Map<K, V>
{
    protected ConcreteValueIndex<K, V> concrete;

    public ValueIndex()
    {
        concrete = createConcrete();
    }

    public ValueIndex(Function<V, K> getKey)
    {
        sane(getKey, "getKey");
        concrete = createConcrete();
        setGetKey(getKey);
    }

    @Override
    public V get(Object key)
    {
        return concrete.get(key);
    }

    public boolean contains(K key, V value)
    {
        return concrete.contains(key, value);
    }

    public boolean contains(K key, Predicate<? super V> match)
    {
        return concrete.contains(key, match);
    }

    public V add(V value)
    {
        return concrete.add(value);
    }

    public V removeMatching(K key, Predicate<? super V> match)
    {
        return concrete.removeMatching(key, match);
    }

    public void reindex(K oldKey, K newKey, V value)
    {
        concrete.reindex(oldKey, newKey, value);
    }

    public void reindex(K oldKey, V value)
    {
        concrete.reindex(oldKey, value);
    }

    public void index(V oldValue, V newValue)
    {
        concrete.index(oldValue, newValue);
    }

    public void setGetKey(Function<V, K> getKey)
    {
        concrete.setGetKey(getKey);
    }

    @Override
    public int size()
    {
        return concrete.size();
    }

    @Override
    public boolean isEmpty()
    {
        return concrete.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return concrete.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return concrete.containsValue(value);
    }

    @Override
    public V put(K key, V value)
    {
        return concrete.put(key, value);
    }

    @Override
    public V remove(Object key)
    {
        return concrete.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m)
    {
        concrete.putAll(m);
    }

    @Override
    public void clear()
    {
        concrete.clear();
    }

    @Override
    public Set<K> keySet()
    {
        return concrete.keySet();
    }

    @Override
    public Collection<V> values()
    {
        return concrete.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return concrete.entrySet();
    }

    @SuppressWarnings("unchecked")
    protected ConcreteValueIndex<K, V> createConcrete()
    {
        return Creator.create(ConcreteValueIndex.class);
    }
}
