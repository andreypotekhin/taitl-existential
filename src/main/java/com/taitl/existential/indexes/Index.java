package com.taitl.existential.indexes;

import static com.taitl.existential.constants.Strings.KEY_ARG;
import static com.taitl.existential.constants.Strings.VALUE_ARG;

import java.util.Set;
import java.util.function.Predicate;

import com.taitl.existential.commons.Multimap;

public class Index<K, V>
{
    Multimap<K, V> storage = new Multimap<K, V>();

    public Set<V> get(K k)
    {
        return storage.get(k);
    }

    public boolean contains(K k)
    {
        return storage.containsKey(k);
    }

    public boolean contains(K k, Predicate<Integer> checkCount)
    {
        Set<V> set = storage.get(k);
        if (set == null || set.isEmpty())
        {
            return false;
        }
        return checkCount.test(set.size());
    }

    public Set<V> add(K k, V v)
    {
        if (k == null)
        {
            throw new IllegalArgumentException(KEY_ARG);
        }
        return storage.put(k, v);
    }

    public V remove(K k, V v)
    {
        if (k == null)
        {
            throw new IllegalArgumentException(KEY_ARG);
        }
        if (v == null)
        {
            throw new IllegalArgumentException(VALUE_ARG);
        }
        return storage.remove(k, v);
    }

    public void changeKey(K k0, K k1, V v)
    {
        if (k0 == null)
        {
            throw new IllegalArgumentException(KEY_ARG);
        }
        if (k1 == null)
        {
            throw new IllegalArgumentException(KEY_ARG);
        }
        if (v == null)
        {
            throw new IllegalArgumentException(VALUE_ARG);
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
        return storage.get((K) key);
    }
}
