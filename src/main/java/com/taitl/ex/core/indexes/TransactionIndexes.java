package com.taitl.ex.core.indexes;

import com.taitl.existential.configs.*;
import com.taitl.existential.indexes.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.sane;

public class TransactionIndexes
{
    protected Transaction transaction;
    protected Map<String, Index<?, ?>> indexes = new ConcurrentHashMap<>();

    public TransactionIndexes(Transaction tr)
    {
        this.transaction = tr;
    }

    public <K, V> Index<K, V> create(String name, Supplier<Index<K, V>> createIndex, Function<V, K> getKey)
    {
        sane(name, "name");
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
        sane(name, "name");
        return (Index<K, V>) indexes.computeIfAbsent(name, ignored -> new Index<>());
    }
}
