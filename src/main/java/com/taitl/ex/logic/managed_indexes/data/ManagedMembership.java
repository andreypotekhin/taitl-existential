package com.taitl.ex.logic.managed_indexes.data;

import java.util.*;

/**
 * Compact identity enrollment and the last materialized groups for one index side.
 * Multi-valued grouping is deferred entirely until access. Single-valued winner metadata
 * captures destructive replacement without replaying reports or retaining displaced entries.
 * Pending identities are bounded by current or materialized membership; physical updates
 * touch only changed members, even when a join key groups a large number of objects.
 */
public class ManagedMembership<K, V>
{
    private final boolean multi;
    private Map<V, K> keys = new IdentityHashMap<>();
    private Map<K, V> winners = new HashMap<>();
    private Map<K, Set<V>> applied = new LinkedHashMap<>();
    private Map<V, K> appliedKeys = new IdentityHashMap<>();
    private Set<V> pending = Collections.newSetFromMap(new IdentityHashMap<>());
    private boolean materialized;
    private long updates;

    public ManagedMembership(boolean multi)
    {
        this.multi = multi;
    }

    public void add(K key, V value)
    {
        K previous = keys.get(value);
        if (previous != null && previous.equals(key))
        {
            return;
        }
        remove(value);
        if (!multi)
        {
            V displaced = winners.put(key, value);
            if (displaced != null)
            {
                keys.remove(displaced);
                changed(displaced);
            }
        }
        keys.put(value, key);
        changed(value);
    }

    public V remove(V value)
    {
        K key = keys.remove(value);
        if (key == null)
        {
            return null;
        }
        if (!multi && winners.get(key) == value)
        {
            winners.remove(key);
        }
        changed(value);
        return value;
    }

    public K key(Object value)
    {
        return keys.get(value);
    }

    public void flush()
    {
        if (materialized && pending.isEmpty())
        {
            return;
        }
        Collection<V> changed = new ArrayList<>(materialized ? pending : keys.keySet());
        for (V value : changed)
        {
            K oldKey = appliedKeys.get(value);
            K newKey = keys.get(value);
            if (oldKey != null && !Objects.equals(oldKey, newKey))
            {
                Set<V> group = applied.get(oldKey);
                group.remove(value);
                if (group.isEmpty())
                {
                    applied.remove(oldKey);
                }
                appliedKeys.remove(value);
                updates++;
            }
        }
        for (V value : changed)
        {
            K key = keys.get(value);
            if (key != null && !appliedKeys.containsKey(value))
            {
                applied.computeIfAbsent(key, ignored -> identitySet()).add(value);
                appliedKeys.put(value, key);
                updates++;
            }
        }
        pending.clear();
        materialized = true;
    }

    public Map<K, Set<V>> applied()
    {
        return applied;
    }

    public Collection<V> values()
    {
        return new ArrayList<>(keys.keySet());
    }

    public void clear()
    {
        for (V value : new ArrayList<>(keys.keySet()))
        {
            remove(value);
        }
    }

    public void close()
    {
        keys.clear();
        winners.clear();
        for (Set<V> group : applied.values())
        {
            group.clear();
        }
        applied.clear();
        appliedKeys.clear();
        pending.clear();
        keys = new IdentityHashMap<>();
        winners = new HashMap<>();
        applied = new LinkedHashMap<>();
        appliedKeys = new IdentityHashMap<>();
        pending = Collections.newSetFromMap(new IdentityHashMap<>());
    }

    public int retainedReferences()
    {
        int count = keys.size() + appliedKeys.size() + winners.size() + pending.size();
        for (Set<V> values : applied.values())
        {
            count += values.size();
        }
        return count;
    }

    public long updates()
    {
        return updates;
    }

    public int pendingChanges()
    {
        return pending.size();
    }

    private void changed(V value)
    {
        if (materialized && (appliedKeys.containsKey(value) || keys.containsKey(value)))
        {
            pending.add(value);
        }
        else
        {
            pending.remove(value);
        }
    }

    private Set<V> identitySet()
    {
        return new IdentityGroup<>();
    }
}
