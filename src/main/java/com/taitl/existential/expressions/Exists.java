package com.taitl.existential.expressions;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;
import com.taitl.existential.transactions.Transaction;

public class Exists<V, K> implements Predicate<Transaction>
{
    // Index<K, V> index;
    // String indexName;
    // K key;
    Set<V> values;
    Predicate<Set<V>> predicate;
    BiPredicate<Set<V>, Transaction> bipredicate;
    Predicate<V> vpredicate;
    BiPredicate<V, Transaction> vbipredicate;

    // public Exists(String index, K key)
    // {
    // this(index, key, (Predicate<Set<V>>) null);
    // }
    //
    // public Exists(String indexName, K key, Predicate<Set<V>> predicate)
    // {
    // Args.cool(indexName, "indexName", key, "key", predicate, "predicate");
    // this.indexName = indexName;
    // this.key = key;
    // this.predicate = predicate;
    // }
    //
    // public Exists(String indexName, K key, BiPredicate<Set<V>, Transaction> bipredicate)
    // {
    // Args.cool(indexName, "indexName", key, "key", bipredicate, "bipredicate");
    // this.indexName = indexName;
    // this.key = key;
    // this.bipredicate = bipredicate;
    // }
    //
    // public Exists(Index<K, V> index, K key, Predicate<Set<V>> predicate)
    // {
    // Args.cool(index, "index", key, "key", predicate, "predicate");
    // this.index = index;
    // this.key = key;
    // this.predicate = predicate;
    // }
    //
    // public Exists(Index<K, V> index, K key, BiPredicate<Set<V>, Transaction> bipredicate)
    // {
    // Args.cool(index, "index", key, "key", bipredicate, "bipredicate");
    // this.index = index;
    // this.key = key;
    // this.bipredicate = bipredicate;
    // }
    //

    public Exists(Set<V> values, Predicate<V> predicate)
    {
        Args.cool(values, "values", predicate, "predicate");
        this.values = values;
        this.vpredicate = predicate;
    }

    public Exists(Set<V> values, BiPredicate<V, Transaction> bipredicate)
    {
        Args.cool(values, "values", bipredicate, "bipredicate");
        this.values = values;
        this.vbipredicate = bipredicate;
    }

    public Exists(Set<V> values, Predicate<Set<V>> predicate, int placeholder)
    {
        Args.cool(values, "values", predicate, "predicate");
        this.values = values;
        this.predicate = predicate;
    }

    public Exists(Set<V> values, BiPredicate<Set<V>, Transaction> bipredicate, int placeholder)
    {
        Args.cool(values, "values", bipredicate, "bipredicate");
        this.values = values;
        this.bipredicate = bipredicate;
    }

    public boolean test(Transaction tran)
    {
        Args.cool(tran, "tran");
        return testByValues(tran);
//        boolean result;
//        // if (index != null || indexName != null)
//        // {
//        // result = testByIndex(tran);
//        // }
//        // else
//        // {
//        result = testByValues(tran);
//        // }
//        return result;
    }

    // protected boolean testByIndex(Transaction tran)
    // {
    // Args.cool(tran, "tran");
    // State.verify((index != null) != (indexName != null),
    // "Index and indexName can't both be non-null");
    // if (index == null)
    // {
    // index = tran.indexes.get(indexName);
    // if (index == null)
    // {
    // throw new RuntimeException("Index not found: " + indexName);
    // }
    // }
    // State.cool(index, "index");
    // Set<V> vals = index.get(key);
    // if (vals == null)
    // {
    // return false;
    // }
    // boolean result;
    // if (predicate != null)
    // {
    // result = predicate.test(values);
    // }
    // else if (bipredicate != null)
    // {
    // result = bipredicate.test(values, tran);
    // }
    // else
    // {
    // result = !values.isEmpty();
    // }
    // return result;
    // }

    protected boolean testByValues(Transaction tran)
    {
        Args.cool(tran, "tran");
        State.cool(values, "values");
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
