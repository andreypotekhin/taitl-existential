package com.taitl.existential.transactions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.taitl.existential.constants.Strings;
import com.taitl.existential.indexes.Index;

public class TransactionIndexes
{
    Transaction tr;
    Map<String, Index<?, ?>> indexes = new LinkedHashMap<>();

    TransactionIndexes(Transaction tr)
    {
        this.tr = tr;
    }

    public <K, V> Index<V, K> create(String name, Supplier<Index<V, K>> createIndex, Function<V, K> getKey)
    {
        if (name == null)
        {
            throw new IllegalArgumentException(Strings.ARG_NAME);
        }
        Index<V, K> index = (createIndex != null) ? createIndex.get() : new Index<>();
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

    public <V, K> Index<V, K> create(String name, Supplier<Index<V, K>> createIndex)
    {
        return create(name, createIndex, null);
    }

    public <V, K> Index<V, K> create(String name)
    {
        return create(name, null, null);
    }

    @SuppressWarnings("unchecked")
    public <V, K> Index<V, K> get(String name)
    {
        return (Index<V, K>) indexes.get(name);
    }
}
