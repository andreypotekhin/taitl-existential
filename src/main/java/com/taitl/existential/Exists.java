package com.taitl.existential;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.taitl.existential.constants.Strings;
import com.taitl.existential.indexes.Index;
import com.taitl.existential.transactions.Transaction;

public class Exists<V, K> implements Predicate<Transaction>
{
    String indexName;
    K key;
    Predicate<Set<V>> predicate;
    BiPredicate<Set<V>, Transaction> bipredicate;

    public Exists(String index, K key)
    {
        this(index, key, (Predicate<Set<V>>) null);
    }

    public Exists(String index, K key, Predicate<Set<V>> predicate)
    {
        if (index == null)
        {
            throw new IllegalArgumentException(Strings.ARG_INDEX);
        }
        if (key == null)
        {
            throw new IllegalArgumentException(Strings.ARG_KEY);
        }
        this.indexName = index;
        this.key = key;
        this.predicate = predicate;
    }

    public Exists(String index, K key, BiPredicate<Set<V>, Transaction> bipredicate)
    {
        if (index == null)
        {
            throw new IllegalArgumentException(Strings.ARG_INDEX);
        }
        if (key == null)
        {
            throw new IllegalArgumentException(Strings.ARG_KEY);
        }
        this.indexName = index;
        this.key = key;
        this.bipredicate = bipredicate;
    }

    public boolean test(Transaction tran)
    {
        if (tran == null)
        {
            throw new IllegalArgumentException(Strings.ARG_TRANSACTION);
        }
        Index<K, V> index = tran.indexes.get(indexName);
        Set<V> values = index.get(key);
        if (values == null)
        {
            return false;
        }

        boolean result;
        if (predicate != null)
        {
            result = predicate.test(values);
        }
        else if (bipredicate != null)
        {
            result = bipredicate.test(values, tran);
        }
        else
        {
            result = !values.isEmpty();
        }
        return result;
    }
}
