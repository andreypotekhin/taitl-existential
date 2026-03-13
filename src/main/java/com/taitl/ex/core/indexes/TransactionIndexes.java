package com.taitl.ex.core.indexes;

import com.taitl.existential.configs.*;
import com.taitl.existential.indexes.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

public class TransactionIndexes
{
    protected Transaction transaction;
    protected Map<String, MultiIndex<?, ?>> indexes = new ConcurrentHashMap<>();

    public TransactionIndexes(Transaction tr)
    {
        this.transaction = tr;
    }

    public <K, V> MultiIndex<K, V> create(String name, Supplier<MultiIndex<K, V>> createIndex, Function<V, K> getKey)
    {
        sane(name, "name", getKey, "getKey");
        MultiIndex<K, V> index = (createIndex != null) ? createIndex.get() : new MultiIndex<>(getKey);
        sane(index, "index");
        if (indexes.putIfAbsent(name, index) != null)
        {
            throw new IllegalStateException(String.format("Index with name '%s' already exists.", name));
        }
        return index;
    }

    @SuppressWarnings("unchecked")
    public <K, V> MultiIndex<K, V> get(String name)
    {
        return (MultiIndex<K, V>) indexes.get(name);
    }

    @SuppressWarnings("unchecked")
    public <K, V> MultiIndex<K, V> getOrCreate(String name, Function<V, K> getKey)
    {
        sane(name, "name", getKey, "getKey");
        return (MultiIndex<K, V>) indexes.computeIfAbsent(name, ignored -> new MultiIndex<>(getKey));
    }
}
