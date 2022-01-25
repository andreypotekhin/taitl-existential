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
    Set<V> values;
    Predicate<Set<V>> predicate;
    BiPredicate<Set<V>, Transaction> bipredicate;
    Predicate<V> vpredicate;
    BiPredicate<V, Transaction> vbipredicate;

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
        if (predicate == null)
        {
            throw new IllegalArgumentException(Strings.ARG_PREDICATE);
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
        if (bipredicate == null)
        {
            throw new IllegalArgumentException(Strings.ARG_BIPREDICATE);
        }
        this.indexName = index;
        this.key = key;
        this.bipredicate = bipredicate;
    }

    public Exists(Set<V> values, Predicate<Set<V>> predicate, int placeholder)
    {
        if (values == null)
        {
            throw new IllegalArgumentException(Strings.ARG_VALUES);
        }
        if (predicate == null)
        {
            throw new IllegalArgumentException(Strings.ARG_PREDICATE);
        }
        this.values = values;
        this.predicate = predicate;
    }

    public Exists(Set<V> values, BiPredicate<Set<V>, Transaction> bipredicate, int placeholder)
    {
        if (values == null)
        {
            throw new IllegalArgumentException(Strings.ARG_VALUES);
        }
        if (bipredicate == null)
        {
            throw new IllegalArgumentException(Strings.ARG_PREDICATE);
        }
        this.values = values;
        this.bipredicate = bipredicate;
    }

    public Exists(Set<V> values, Predicate<V> predicate)
    {
        if (values == null)
        {
            throw new IllegalArgumentException(Strings.ARG_VALUES);
        }
        if (predicate == null)
        {
            throw new IllegalArgumentException(Strings.ARG_PREDICATE);
        }
        this.values = values;
        this.vpredicate = predicate;
    }

    public Exists(Set<V> values, BiPredicate<V, Transaction> bipredicate)
    {
        if (values == null)
        {
            throw new IllegalArgumentException(Strings.ARG_VALUES);
        }
        if (bipredicate == null)
        {
            throw new IllegalArgumentException(Strings.ARG_BIPREDICATE);
        }
        this.values = values;
        this.vbipredicate = bipredicate;
    }

    public boolean test(Transaction tran)
    {
        if (tran == null)
        {
            throw new IllegalArgumentException(Strings.ARG_TRANSACTION);
        }
        boolean result;
        if (indexName != null)
        {
            result = testByIndex(tran);
        }
        else
        {
            result = testByValues(tran);
        }
        return result;
    }

    protected boolean testByIndex(Transaction tran)
    {
        if (tran == null)
        {
            throw new IllegalArgumentException(Strings.ARG_TRANSACTION);
        }
        if (indexName == null)
        {
            throw new IllegalStateException(Strings.STATE_INDEX_NAME);
        }
        Index<K, V> index = tran.indexes.get(indexName);
        Set<V> vals = index.get(key);
        if (vals == null)
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

    protected boolean testByValues(Transaction tran)
    {
        if (tran == null)
        {
            throw new IllegalArgumentException(Strings.ARG_TRANSACTION);
        }
        if (values == null)
        {
            throw new IllegalStateException(Strings.STATE_VALUES);
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
        else if (values.isEmpty())
        {
            result = false;
        }
        else
        {
            result = false;
            for (V value : values)
            {
                if (vpredicate != null)
                {
                    result = vpredicate.test(value);
                }
                else if (vbipredicate != null)
                {
                    result = vbipredicate.test(value, tran);
                }
                if (result)
                {
                    break;
                }
            }
        }
        return result;
    }
}
