package com.taitl.existential.unused;

import java.util.Set;
import java.util.function.Predicate;

import com.taitl.existential.constants.Strings;
import com.taitl.existential.indexes.Index;
import com.taitl.existential.transactions.Transaction;

public class Exists0<V> implements Predicate<Transaction>
{
    Set<V> set;
    Index<?, V> index;
    Predicate<Set<V>> predicate;
    String indexName;
    Object key;

    public Exists0(Set<V> set)
    {
        if (set == null)
        {
            throw new IllegalArgumentException(Strings.ARG_SET);
        }
        this.set = set;
    }

    public Exists0(Set<V> set, Predicate<Set<V>> predicate)
    {
        if (set == null)
        {
            throw new IllegalArgumentException(Strings.ARG_SET);
        }
        if (predicate == null)
        {
            throw new IllegalArgumentException(Strings.ARG_PREDICATE);
        }
        this.set = set;
        this.predicate = predicate;
    }

    public <K> Exists0(Index<K, V> index, K key)
    {
        if (index == null)
        {
            throw new IllegalArgumentException(Strings.ARG_INDEX);
        }
        if (key == null)
        {
            throw new IllegalArgumentException(Strings.ARG_KEY);
        }
        this.index = index;
        this.key = key;
    }

    public <K> Exists0(Index<K, V> index, K key, Predicate<Set<V>> predicate)
    {
        if (index == null)
        {
            throw new IllegalArgumentException(Strings.ARG_INDEX);
        }
        if (key == null)
        {
            throw new IllegalArgumentException(Strings.ARG_KEY);
        }
        if (predicate == null)
        {
            throw new IllegalArgumentException(Strings.ARG_PREDICATE);
        }
        this.index = index;
        this.key = key;
        this.predicate = predicate;
    }

    public boolean test(Transaction tran)
    {
        if (tran == null)
        {
            throw new IllegalArgumentException(Strings.ARG_TRANSACTION);
        }
        Set<V> values = null;
        if (set != null)
        {
            values = set;
        }
        else if (index != null)
        {
            values = index.getObj(key);
        }
        if (values == null)
        {
            return false;
        }

        boolean result;
        if (predicate != null)
        {
            result = predicate.test(values);
        }
        else
        {
            result = (values != null) && !values.isEmpty();
        }
        return result;
    }
}