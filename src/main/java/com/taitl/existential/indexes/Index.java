package com.taitl.existential.indexes;

import com.taitl.ex.common.helper.collections.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Maps a key (K) to a set of values (V), to make Exists expressions more performant.
 * Note: null is not allowed as a key or as a value.
 *
 * @param <K>
 *            Key type
 * @param <V>
 *            Value type
 */
public class Index<K, V>
{
    protected static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#index-key-mismatch";
    protected static final String ARG_KEY_CLASS = "Argument 'key' class '%s' does not match the key class '%s'"
            + " required by this index. See " + TROUBLESHOOTING_SECTION;
    protected static final String ARG_KEY_VALUE = "Argument 'newKey' value '%s' does not match key value '%s'"
            + " returned by 'getKey' function. See " + TROUBLESHOOTING_SECTION;

    protected SetMap<K, V> storage = new SetMap<>();
    protected Function<V, K> getKey;

    protected static final String NEED_GET_KEY = "You need to call 'setGetKey()' first";

    /**
     * Creates an empty index without a key extractor.
     * Call {@link #setGetKey(Function)} or use {@link #add(Object, Object)}.
     */
    public Index()
    {
    }

    /**
     * Creates an index with a key extractor for value-based inserts.
     *
     * @param getKey Function to extract keys from values
     */
    public Index(Function<V, K> getKey)
    {
        sane(getKey, "getKey");
        setGetKey(getKey);
    }

    /**
     * Returns the values stored under the provided key.
     *
     * @param key Key to look up
     * @return Set of values for the key
     */
    public Set<V> get(K key)
    {
        sane(key, "key");
        return storage.get(key);
    }

    /**
     * Returns true if the key is present in the index.
     *
     * @param key Key to check
     * @return True when the key exists in the index
     */
    public boolean contains(K key)
    {
        sane(key, "key");
        return storage.containsKey(key);
    }

    /**
     * Returns true if the key exists and the value is stored under it.
     *
     * @param key   Key to check
     * @param value Value to check
     * @return True when the value is present under the key
     */
    public boolean contains(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        Set<V> set = storage.get(key);
        if (set == null || set.isEmpty())
        {
            return false;
        }
        return set.contains(value);
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
        sane(key, "key");
        sane(match, "match");
        Set<V> set = storage.get(key);
        if (set == null || set.isEmpty())
        {
            return false;
        }
        return match.test(set);
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
        sane(k, "key");
        sane(v, "value");
        return storage.put(k, v);
    }

    /**
     * Adds a value using the configured key extractor.
     *
     * @param v Value to add
     * @return Set of values stored under the derived key
     */
    public Set<V> add(V v)
    {
        sane(v, "value");
        verify(getKey != null, NEED_GET_KEY);
        return storage.put(getKey.apply(v), v);
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
    public V remove(K k, V v)
    {
        sane(k, "key");
        sane(v, "value");
        return storage.remove(k, v);
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
        sane(k, "key");
        sane(match, "match");
        return storage.remove(k, match);
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
        sane(k0, "oldKey");
        sane(k1, "newKey");
        sane(v, "value");
        if (getKey != null)
        {
            K k = getKey.apply(v);
            if (!k1.equals(k))
            {
                throw new IllegalArgumentException(String.format(
                        ARG_KEY_VALUE,
                        k1, k));
            }
        }
        synchronized (this)
        {
            remove(k0, v);
            add(k1, v);
        }
    }

    /**
     * Reinserts a value using the current key extracted by {@link #setGetKey(Function)}.
     *
     * @param k0 Old key
     * @param v Value to reindex
     */
    public void reindex(K k0, V v)
    {
        sane(k0, "oldKey");
        sane(v, "value");
        verify(getKey != null, NEED_GET_KEY);
        reindex(k0, getKey.apply(v), v);
    }

    /**
     * In some scenarios, the exact key type is not known.
     * This provides a method to query by an {@link Object} key.
     *
     * @param key
     *            Object representing a key
     * @return Set of values stored under the key
     */
    @SuppressWarnings("unchecked")
    public Set<V> getObj(Object key)
    {
        sane(key, "key");
        if (storage.size() == 0)
        {
            return null;
        }
        Class<? extends K> keyClass = storage.getKeyClass();
        verify(keyClass.isAssignableFrom(key.getClass()),
                String.format(ARG_KEY_CLASS,
                        key.getClass().getSimpleName(), keyClass.getSimpleName()));
        return storage.get((K) key);
    }

    /**
     * Sets the key extractor used by {@link #add(Object)}.
     *
     * @param getKey Function to extract keys from values
     */
    public void setGetKey(Function<V, K> getKey)
    {
        sane(getKey, "getKey");
        this.getKey = getKey;
    }

    /**
     * Removes all entries from the index.
     */
    public void clear()
    {
        storage.clear();
    }

    /**
     * Returns amount of keys that currently have at least one value.
     */
    public int size()
    {
        return storage.size();
    }
}
