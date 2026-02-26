package com.taitl.ex.core.transactions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.indexes.*;

public class TransactionIndexes
{
    Transaction tr;
    Map<String, Index<?, ?>> indexes = new ConcurrentHashMap<>();

    public TransactionIndexes(Transaction tr)
    {
        this.tr = tr;
    }

    public <K, V> Index<K, V> create(String name, Supplier<Index<K, V>> createIndex, Function<V, K> getKey)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Argument 'name' should not be null");
        }
        Index<K, V> index = (createIndex != null) ? createIndex.get() : new Index<>();
        if (getKey != null)
        {
            index.setGetKey(getKey);
        }
        if (indexes.putIfAbsent(name, index) != null)
        {
            throw new IllegalStateException(String.format("Index with name '%s' already exists.", name));
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

    @SuppressWarnings("unchecked")
    public <K, V> Index<K, V> getOrCreate(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Argument 'name' should not be null");
        }
        return (Index<K, V>) indexes.computeIfAbsent(name, ignored -> new Index<>());
    }
}
