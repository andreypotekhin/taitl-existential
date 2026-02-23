package com.taitl.existential.indexes;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.common.helper.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Index maps a single key (K) to a set of values (V).
 * Indexes make Exists expressions more performant.
 * Note: null is not allowed as a key or as a value.
 *
 * @param <K>
 *            Key type
 * @param <V>
 *            Value type
 */
public class Index<K, V>
{
    private static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#index-key-mismatch";
    private static final String ARG_KEY_CLASS = "Argument 'key' class '%s' does not match the key class '%s'"
            + " required by this index. See " + TROUBLESHOOTING_SECTION;
    private static final String ARG_KEY_VALUE = "Argument 'k1' value '%s' does not match key value '%s'"
            + " returned for object 'v' by 'getKey' function. See " + TROUBLESHOOTING_SECTION;

    protected Multimap<K, V> storage = new Multimap<>();
    protected Function<V, K> getKey;

    public Index()
    {
    }

    public Index(Function<V, K> getKey)
    {
        sane(getKey, "getKey");
        setGetKey(getKey);
    }

    public Set<V> get(K key)
    {
        sane(key, "key");
        return storage.get(key);
    }

    public boolean contains(K key)
    {
        sane(key, "key");
        return storage.containsKey(key);
    }

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

    public Set<V> add(K k, V v)
    {
        sane(k, "k");
        sane(v, "v");
        return storage.put(k, v);
    }

    public Set<V> add(V v)
    {
        sane(v, "v");
        verify(getKey != null, "You need to call 'setGetKey()' first");
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
        sane(k, "k");
        sane(v, "v");
        return storage.remove(k, v);
    }

    public Set<V> remove(K k, Predicate<? super V> match)
    {
        sane(k, "k");
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
    public void rekey(K k0, K k1, V v)
    {
        sane(k0, "k0");
        sane(k1, "k1");
        sane(v, "v");
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

    public void setGetKey(Function<V, K> getKey)
    {
        sane(getKey, "getKey");
        this.getKey = getKey;
    }

    public void clear()
    {
        storage.clear();
    }
}
