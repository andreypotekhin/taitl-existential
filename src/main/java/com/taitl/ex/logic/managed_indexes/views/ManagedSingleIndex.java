package com.taitl.ex.logic.managed_indexes.views;

import com.taitl.ex.logic.managed_indexes.data.*;
import com.taitl.existential.indexes.*;
import java.util.*;
import java.util.function.*;
import static com.taitl.ex.common.helper.Args.*;

/** Adapts the existing SingleIndex facade to one guarded transaction-local membership owner. */
public class ManagedSingleIndex<K, V> extends SingleIndex<K, V>
{
    private final ManagedSide<K, V> side;
    private final Map<K, V> query;

    public ManagedSingleIndex(ManagedSide<K, V> side, ManagedAccess access)
    {
        super(value -> side.extract(value));
        this.side = side;
        query = new ManagedQueryMap<>(access, key -> side.single(key), () -> side.keySet());
    }

    public V get(Object key)
    {
        return side.single(key);
    }

    public boolean contains(V value)
    {
        return side.contains(value);
    }

    public boolean contains(K key, Predicate<? super V> match)
    {
        sane(match, "match");
        V value = get(key);
        return value != null && match.test(value);
    }

    public V add(V value)
    {
        side.accept(side.extract(value), value);
        return value;
    }

    public void addAll(Collection<? extends V> values)
    {
        side.addAll(values);
    }

    public void index(V before, V after)
    {
        side.index(before, after);
    }

    public void reindex(K oldKey, K newKey, V value)
    {
        side.reindex(oldKey, newKey, value);
    }

    public void reindex(K oldKey, V value)
    {
        side.reindex(oldKey, side.extract(value), value);
    }

    public int size()
    {
        return side.keySet().size();
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public boolean containsKey(Object key)
    {
        return get(key) != null;
    }

    public boolean containsValue(Object value)
    {
        return side.contains(value);
    }

    public V removeMatching(K key, Predicate<? super V> match)
    {
        sane(match, "match");
        V value = get(key);
        return value != null && match.test(value) ? side.remove(key, value) : null;
    }

    public V put(K key, V value)
    {
        V previous = get(key);
        side.accept(key, value);
        return previous;
    }

    public V remove(Object key)
    {
        V previous = get(key);
        if (previous != null)
        {
            side.remove(previous);
        }
        return previous;
    }

    public boolean remove(Object key, Object value)
    {
        V previous = get(key);
        if (previous != value || previous == null)
        {
            return false;
        }
        side.remove(previous);
        return true;
    }

    public void putAll(Map<? extends K, ? extends V> values)
    {
        sane(values, "values");
        Map<K, V> copy = new LinkedHashMap<>();
        values.forEach((key, value) -> {
            sane(key, "key", value, "value");
            copy.put(key, value);
        });
        copy.forEach((key, value) -> put(key, value));
    }

    public void clear()
    {
        side.clear();
    }

    public Set<K> keySet()
    {
        return query.keySet();
    }

    public Collection<V> values()
    {
        return query.values();
    }

    public Set<Map.Entry<K, V>> entrySet()
    {
        return query.entrySet();
    }
}
