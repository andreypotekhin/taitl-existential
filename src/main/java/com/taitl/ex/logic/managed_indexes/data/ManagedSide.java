package com.taitl.ex.logic.managed_indexes.data;

import com.taitl.existential.indexes.*;
import com.taitl.existential.keys.*;
import java.util.*;
import java.util.function.*;
import static com.taitl.ex.common.helper.Args.*;

/**
 * Owns one subject side's captured keys, folded membership, and guarded group queries.
 * Single-valued keys are captured on acceptance; multi-valued keys are validated as a batch
 * when pending changes flush. Only explicit accesses or boundaries materialize the groups.
 * Explicit facade mutations use the same ordering as automatic changes and seed loading.
 */
public class ManagedSide<K, V>
{
    private IndexDefinition<?> definition;
    private final boolean right;
    private final ManagedAccess access;
    private final ManagedMembership<K, V> membership;
    private long extractions;
    private final boolean multi;
    private final DeferredIndexChanges<K, V> deferred = new DeferredIndexChanges<>();

    public ManagedSide(IndexDefinition<?> definition, boolean right, boolean multi, ManagedAccess access)
    {
        this.definition = definition;
        this.right = right;
        this.access = access;
        this.multi = multi;
        membership = new ManagedMembership<>(multi);
    }

    public TypeKey<?> type()
    {
        return definition.type(right);
    }

    @SuppressWarnings("unchecked")
    public K extract(Object value)
    {
        access.check();
        try
        {
            sane(value, "value");
            K key = (K) definition.key(right, value);
            sane(key, "managed index key");
            extractions++;
            return key;
        }
        catch (RuntimeException | Error failure)
        {
            throw access.failure(definition.name() + (right ? " (right)" : " (left)"), failure);
        }
    }

    public void accept(K key, V value)
    {
        access.check();
        flushDeferred();
        sane(key, "key", value, "value");
        try
        {
            membership.add(key, value);
        }
        catch (RuntimeException | Error failure)
        {
            throw access.failure(definition.name(), failure);
        }
    }

    public V remove(V value)
    {
        access.check();
        sane(value, "value");
        flushDeferred();
        return membership.remove(value);
    }

    public V remove(K key, V value)
    {
        access.check();
        flushDeferred();
        return Objects.equals(key, membership.key(value)) ? membership.remove(value) : null;
    }

    public void index(V before, V after)
    {
        access.check();
        K key = after == null ? null : extract(after);
        if (before != null)
        {
            remove(before);
        }
        if (after != null)
        {
            accept(key, after);
        }
    }

    public void addAll(Collection<? extends V> values)
    {
        access.check();
        sane(values, "values");
        List<V> copy = new ArrayList<>(values);
        List<K> keys = new ArrayList<>(copy.size());
        for (V value : copy)
        {
            keys.add(extract(value));
        }
        for (int i = 0; i < copy.size(); i++)
        {
            accept(keys.get(i), copy.get(i));
        }
    }

    public void reindex(K oldKey, K newKey, V value)
    {
        access.check();
        flushDeferred();
        if (!Objects.equals(newKey, extract(value)))
        {
            throw access.failure(definition.name(), new IllegalArgumentException("New key must match key extractor"));
        }
        if (Objects.equals(oldKey, membership.key(value)))
        {
            accept(newKey, value);
        }
    }

    public boolean contains(Object value)
    {
        access.check();
        flushDeferred();
        return membership.key(value) != null;
    }

    public K key(Object value)
    {
        access.check();
        flushDeferred();
        return membership.key(value);
    }

    public V single(Object key)
    {
        Set<V> group = group(key);
        return group == null || group.isEmpty() ? null : group.iterator().next();
    }

    public Set<V> group(Object key)
    {
        flush();
        Set<V> group = membership.applied().get(key);
        return group == null ? null : new ManagedGroupSet<>(access, group, () -> flush());
    }

    public Set<K> keySet()
    {
        flush();
        return new LinkedHashSet<>(membership.applied().keySet());
    }

    public Collection<V> values()
    {
        access.check();
        flushDeferred();
        return membership.values();
    }

    public Set<V> removeKey(Object key)
    {
        Set<V> group = group(key);
        if (group == null)
        {
            return null;
        }
        Set<V> previous = Collections.newSetFromMap(new IdentityHashMap<>());
        previous.addAll(group);
        for (V value : previous)
        {
            remove(value);
        }
        return Collections.unmodifiableSet(previous);
    }

    public void clear()
    {
        access.check();
        deferred.close();
        membership.clear();
    }

    public void flush()
    {
        access.check();
        try
        {
            flushDeferred();
            membership.flush();
        }
        catch (RuntimeException | Error failure)
        {
            throw access.failure(definition.name(), failure);
        }
    }

    public void close()
    {
        deferred.close();
        membership.close();
        definition = null;
    }

    public int retainedReferences()
    {
        return membership.retainedReferences() + deferred.size();
    }

    public boolean deferred()
    {
        return multi;
    }

    public void enroll(V before, V after)
    {
        access.check();
        deferred.enroll(before, after, membership);
    }

    private void flushDeferred()
    {
        try
        {
            deferred.flush(membership, value -> extract(value));
        }
        catch (RuntimeException | Error failure)
        {
            throw access.failure(definition.name(), failure);
        }
    }

    public long updates()
    {
        return membership.updates();
    }

    public long extractions()
    {
        return extractions;
    }
}
