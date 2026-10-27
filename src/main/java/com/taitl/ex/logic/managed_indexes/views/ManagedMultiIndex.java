package com.taitl.ex.logic.managed_indexes.views;

import com.taitl.ex.logic.managed_indexes.data.*;
import com.taitl.existential.indexes.*;
import java.util.*;
import java.util.function.*;
import static com.taitl.ex.common.helper.Args.*;

/** Adapts the existing MultiIndex facade to one guarded transaction-local membership owner. */
public class ManagedMultiIndex<K, V> extends MultiIndex<K, V>
{
    private final ManagedSide<K, V> side;
    private final Map<K, Set<V>> query;

    public ManagedMultiIndex(ManagedSide<K, V> side, ManagedAccess access)
    {
        super(value -> side.extract(value));
        this.side = side;
        query = new ManagedQueryMap<>(access, key -> side.group(key), () -> side.keySet());
    }

    public Set<V> get(Object key)
    {
        return side.group(key);
    }

    public boolean contains(V value)
    {
        return side.contains(value);
    }

    public boolean contains(K key, Predicate<Set<V>> match)
    {
        sane(match, "match");
        Set<V> value = get(key);
        return value != null && match.test(value);
    }

    public Set<V> add(V value)
    {
        side.accept(side.extract(value), value);
        return side.group(side.key(value));
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
        return query.containsValue(value);
    }

    public Set<V> add(K key, V value)
    {
        side.accept(key, value);
        return side.group(key);
    }

    public V removeValue(K key, V value)
    {
        return side.remove(key, value);
    }

    public Set<V> remove(K key, Predicate<? super V> match)
    {
        sane(match, "match");
        Set<V> values = get(key);
        if (values == null)
        {
            return null;
        }
        Set<V> removed = Collections.newSetFromMap(new IdentityHashMap<>());
        for (V value : new ArrayList<>(values))
        {
            if (match.test(value))
            {
                removed.add(value);
            }
        }
        for (V value : removed)
        {
            side.remove(key, value);
        }
        return removed.isEmpty() ? null : Collections.unmodifiableSet(removed);
    }

    public Set<V> put(K key, Set<V> values)
    {
        sane(key, "key", values, "values");
        List<V> copy = new ArrayList<>(values);
        for (V value : copy)
        {
            sane(value, "value");
        }
        Set<V> previous = side.removeKey(key);
        for (V value : copy)
        {
            side.accept(key, value);
        }
        return previous;
    }

    public Set<V> remove(Object key)
    {
        return side.removeKey(key);
    }

    public void putAll(Map<? extends K, ? extends Set<V>> values)
    {
        sane(values, "values");
        Map<K, Set<V>> copy = new LinkedHashMap<>();
        values.forEach((key, group) -> {
            sane(key, "key", group, "group");
            Set<V> members = Collections.newSetFromMap(new IdentityHashMap<>());
            for (V member : group)
            {
                sane(member, "member");
                members.add(member);
            }
            copy.put(key, members);
        });
        copy.forEach((key, group) -> put(key, group));
    }

    public void clear()
    {
        side.clear();
    }

    public Set<K> keySet()
    {
        return query.keySet();
    }

    public Collection<Set<V>> values()
    {
        return query.values();
    }

    public Set<Map.Entry<K, Set<V>>> entrySet()
    {
        return query.entrySet();
    }
}
