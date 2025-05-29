package com.taitl.existential.transactions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.taitl.existential.constants.Strings;
import com.taitl.exlogic.unused.indexes.Index;

public class TransactionIndexes
{
    Transaction tr;
    Map<String, Index<?, ?>> indexes = new LinkedHashMap<>();

    TransactionIndexes(Transaction tr)
    {
        this.tr = tr;
    }

    public <K, V> Index<K, V> create(String name, Supplier<Index<K, V>> createIndex, Function<V, K> getKey)
    {
        if (name == null)
        {
            throw new IllegalArgumentException(Strings.ARG_NAME);
        }
        Index<K, V> index = (createIndex != null) ? createIndex.get() : new Index<>();
        if (getKey != null)
        {
            index.setGetKey(getKey);
        }
        synchronized (indexes)
        {
            if (indexes.containsKey(name))
            {
                throw new IllegalStateException(String.format("Index with name '%s' already exists.", name));
            }
            indexes.put(name, index);
        }
        return index;
    }

    public <K, V> Index<K, V> create(String name, Supplier<Index<K, V>> createIndex)
    {
        return create(name, createIndex, null);
    }

    public <K, V> Index<K, V> create(String name)
    {
        return create(name, null, null);
    }

    @SuppressWarnings("unchecked")
    public <K, V> Index<K, V> get(String name)
    {
        return (Index<K, V>) indexes.get(name);
    }
}
