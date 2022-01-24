package com.taitl.existential.indexes;

import static com.taitl.existential.constants.Strings.ARG_CHECK;
import static com.taitl.existential.constants.Strings.ARG_KEY;
import static com.taitl.existential.constants.Strings.ARG_VALUE;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.taitl.existential.commons.Multimap;
import com.taitl.existential.constants.Strings;

public class Index<V, K>
{
    protected static final String ARG_GET_KEY = "Argument 'getKey' must not be null.";
    protected static final String REQUIRE_SET_GET_KEY = "To call this method, you need to call 'setGetKey()' first";

    protected Multimap<K, V> storage = new Multimap<>();
    protected Function<V, K> getKey;
    
    public Index(){}
    
    public Index(Function<V, K> getKey){ 
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
            throw new IllegalArgumentException(Strings.ARG_KEY);
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

    public boolean contains(K key, Predicate<Set<V>> check)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(ARG_KEY);
        }
        if (check == null)
        {
            throw new IllegalArgumentException(ARG_CHECK);
        }
        Set<V> set = storage.get(key);
        if (set == null || set.isEmpty())
        {
            return false;
        }
        return check.test(set);
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
            throw new IllegalStateException(REQUIRE_SET_GET_KEY);
        }
        return storage.put(getKey.apply(v), v);
    }

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

    public void setGetKey(Function<V, K> getKey)
    {
        this.getKey = getKey;
    }
}
