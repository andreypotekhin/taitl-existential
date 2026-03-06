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
    protected Map<String, SetIndex<?, ?>> indexes = new ConcurrentHashMap<>();

    public TransactionIndexes(Transaction tr)
    {
        this.transaction = tr;
    }

    public <K, V> SetIndex<K, V> create(String name, Supplier<SetIndex<K, V>> createIndex, Function<V, K> getKey)
    {
        sane(name, "name", getKey, "getKey");
        SetIndex<K, V> index = (createIndex != null) ? createIndex.get() : new SetIndex<>(getKey);
        sane(index, "index");
        index.setGetKey(getKey);
        if (indexes.putIfAbsent(name, index) != null)
        {
            throw new IllegalStateException(String.format("Index with name '%s' already exists.", name));
        }
        return index;
    }

    @SuppressWarnings("unchecked")
    public <K, V> SetIndex<K, V> get(String name)
    {
        return (SetIndex<K, V>) indexes.get(name);
    }

    @SuppressWarnings("unchecked")
    public <K, V> SetIndex<K, V> getOrCreate(String name, Function<V, K> getKey)
    {
        sane(name, "name", getKey, "getKey");
        return (SetIndex<K, V>) indexes.computeIfAbsent(name, ignored -> new SetIndex<>(getKey));
    }
}
