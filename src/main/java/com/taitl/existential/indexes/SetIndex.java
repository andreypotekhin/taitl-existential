package com.taitl.existential.indexes;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Dynamic index optimized for quick retrieval of a set of values Set<V>
 * based on a key function K(V).
 * Internally, it uses a Map<K, Set<V>> to store and map the values.
 * The key function K(V) is specified once at construction time.
 * The index is dynamic in the sense of allowing to change the
 * key of a value without need to reinsert.
 * Note: null is not allowed as a key or as a value.
 *
 * Usage:
 * boolean b = index.contains(value);
 * Set<V> value = index.get(key);
 *
 * @param <K>
 *            Key type
 * @param <V>
 *            Value type
 */
public class SetIndex<K, V> implements Map<K, Set<V>>
{
    protected ConcreteSetIndex<K, V> concrete;

    /**
     * Creates an index with a key extractor for value-based inserts.
     *
     * @param getKey Function to extract keys from values
     */
    public SetIndex(Function<V, K> getKey)
    {
        sane(getKey, "getKey");
        concrete = createConcrete(getKey);
    }

    /**
     * Indexes value transition with null-tolerant semantics.
     */
    public void index(V oldValue, V newValue)
    {
        concrete.index(oldValue, newValue);
    }

    /**
     * Returns the values stored under the provided key.
     *
     * @param key Key to look up
     * @return Set of values for the key
     */
    @Override
    public Set<V> get(Object key)
    {
        return concrete.get(key);
    }

    /**
     * Returns true if the value is indexed under its derived key.
     *
     * @param value Value to check
     * @return True when the value is indexed
     */
    public boolean contains(V value)
    {
        return concrete.contains(value);
    }

    /**
     * Returns true if the key exists and the set of values matches the predicate.
     *
     * @param key   Key to check
     * @param match Predicate applied to the value set
     * @return True when the predicate matches
     */
    public boolean contains(K key, Predicate<Set<V>> match)
    {
        return concrete.contains(key, match);
    }

    /**
     * Adds a value using the configured key extractor.
     *
     * @param v Value to add
     * @return Set of values stored under the derived key
     */
    public Set<V> add(V v)
    {
        return concrete.add(v);
    }

    public void addAll(Collection<? extends V> values)
    {
        concrete.addAll(values);
    }

    /**
     * Adds a value under the provided key.
     *
     * @param k Key to add under
     * @param v Value to add
     * @return Set of values stored under the key
     */
    public Set<V> add(K k, V v)
    {
        return concrete.add(k, v);
    }

    /**
     * Removes a key-value pair from the multimap backing the index.
     * If other items exist for the same key, they remain intact.
     * Returns the value that was removed, or null if there is no value for this key.
     *
     * @param k
     *            Key for the value
     * @param v
     *            Value to be removed
     * @return The value being removed, or null if the value is not in the index
     */
    public V removeValue(K k, V v)
    {
        return concrete.removeValue(k, v);
    }

    /**
     * Removes values under a key that match the predicate.
     *
     * @param k     Key to remove under
     * @param match Predicate to match values for removal
     * @return Set of removed values
     */
    public Set<V> remove(K k, Predicate<? super V> match)
    {
        return concrete.remove(k, match);
    }

    /**
     * Reinserts a value under a different key.
     * Used when the key is a field on the value object and that field has changed.
     *
     * @param k0 Old key
     * @param k1 New key
     * @param v Value
     */
    public void reindex(K k0, K k1, V v)
    {
        concrete.reindex(k0, k1, v);
    }

    /**
     * Reinserts a value using the key extracted from value.
     *
     * @param k0 Old key
     * @param v Value to reindex
     */
    public void reindex(K k0, V v)
    {
        concrete.reindex(k0, v);
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
    public Set<V> put(K key, Set<V> value)
    {
        return concrete.put(key, value);
    }

    @Override
    public Set<V> remove(Object key)
    {
        return concrete.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Set<V>> m)
    {
        concrete.putAll(m);
    }

    /**
     * Removes all entries from the index.
     */
    @Override
    public void clear()
    {
        concrete.clear();
    }

    /**
     * Returns amount of keys that currently have at least one value.
     */
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
    public Set<K> keySet()
    {
        return concrete.keySet();
    }

    @Override
    public Collection<Set<V>> values()
    {
        return concrete.values();
    }

    @Override
    public Set<Entry<K, Set<V>>> entrySet()
    {
        return concrete.entrySet();
    }

    @SuppressWarnings("unchecked")
    protected ConcreteSetIndex<K, V> createConcrete(Function<V, K> getKey)
    {
        return Creator.create(ConcreteSetIndex.class,
                new Class<?>[] { Function.class },
                getKey);
    }
}
