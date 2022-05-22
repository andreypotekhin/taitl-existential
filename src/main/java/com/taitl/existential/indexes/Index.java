package com.taitl.existential.indexes;

import static com.taitl.existential.constants.Strings.ARG_CHECK;
import static com.taitl.existential.constants.Strings.ARG_KEY;
import static com.taitl.existential.constants.Strings.ARG_VALUE;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.taitl.existential.helper.Multimap;

/**
 * An indexing structure which provides performance for Exists<> expression.
 * Implements an index (mapping) of a key to a set of multiple values.
 * <p>
 * Note: null is not allowed, neither as a key nor as a value.
 *
 * @author Andrey Potekhin
 *
 * @param <K>
 * @param <V>
 *
 * @see Index
 */
public class Index<K, V>
{
    protected static final String ARG_GET_KEY = "Argument 'getKey' must not be null.";
    protected static final String REQUIRE_SET_GET_KEY = "To call this method, you need to call 'setGetKey()' first";
    private static final String ARG_KEY_CLASS = "Argument 'key' class '%s' does not match the key class '%s'"
            + " reqired by this index.";
    private static final String ARG_KEY_VALUE = "Argument 'k1' value '%s' does not match key value '%s'"
            + " returned for object 'v' by 'getKey' function.";

    protected Multimap<K, V> storage = new Multimap<>();
    protected Function<V, K> getKey;

    public Index()
    {
    }

    public Index(Function<V, K> getKey)
    {
        if (getKey == null)
        {
            throw new IllegalArgumentException(ARG_GET_KEY);
        }
        setGetKey(getKey);
    }

    public Set<V> get(K key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        return storage.get(key);
    }

    public boolean contains(K key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        return storage.containsKey(key);
    }

    public boolean contains(K key, V value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (value == null)
        {
            throw new IllegalArgumentException(ARG_VALUE);
        }
        Set<V> set = storage.get(key);
        if (set == null || set.isEmpty())
        {
            return false;
        }
        return set.contains(value);
    }

    public boolean contains(K key, Predicate<Set<V>> match)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (match == null)
        {
            throw new IllegalArgumentException(ARG_CHECK);
        }
        Set<V> set = storage.get(key);
        if (set == null || set.isEmpty())
        {
            return false;
        }
        return match.test(set);
    }

    public Set<V> add(K k, V v)
    {
        if (k == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        return storage.put(k, v);
    }

    public Set<V> add(V v)
    {
        if (v == null)
        {
            throw new IllegalArgumentException(ARG_VALUE);
        }
        if (getKey == null)
        {
            throw new IllegalArgumentException(REQUIRE_SET_GET_KEY);
        }
        return storage.put(getKey.apply(v), v);
    }

    /**
     * Removes key-value pair from multimap backing the index.
     * If there are other elements under the same key exist in the multimap, they remain intact.
     * <p>
     * Returns the value being removed, or null if value is not in index.
     *
     * @param v Value to be removed
     * @param k Key for the value
     * @return The value being removed, or null if value is not in index
     */
    public V remove(K k, V v)
    {
        if (k == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (v == null)
        {
            throw new IllegalArgumentException(ARG_VALUE);
        }
        return storage.remove(k, v);
    }

    public Set<V> remove(K k, Predicate<? super V> match)
    {
        if (k == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (match == null)
        {
            throw new IllegalArgumentException(ARG_VALUE);
        }
        return storage.remove(k, match);
    }

    public void changeKey(K k0, K k1, V v)
    {
        if (k0 == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (k1 == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (v == null)
        {
            throw new IllegalArgumentException(ARG_VALUE);
        }
        if (getKey != null)
        {
            K k = getKey.apply(v);
            if (k1 != k)
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
     * In some scenarios, the exact type of key is not known. Provide a method to query by an Object key.
     *
     * @param key
     *            Object representing a key
     * @return Set of values stored under key
     */
    @SuppressWarnings("unchecked")
    public Set<V> getObj(Object key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (storage.size() == 0)
        {
            return null;
        }
        Class<? extends K> keyClass = storage.getKeyClass();
        if (!keyClass.isAssignableFrom(key.getClass()))
        {
            throw new IllegalArgumentException(String.format(
                    ARG_KEY_CLASS,
                    key.getClass().getSimpleName(), keyClass.getSimpleName()));
        }
        return storage.get((K) key);
    }

    public void setGetKey(Function<V, K> getKey)
    {
        this.getKey = getKey;
    }

    public void clear()
    {
        storage.clear();
    }
}
