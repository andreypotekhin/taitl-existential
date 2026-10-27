package com.taitl.ex.logic.managed_indexes.views;

import com.taitl.ex.logic.managed_indexes.data.*;
import java.util.*;
import java.util.function.*;

/** A live guarded query projection; mutation cannot bypass the owning facade's maintenance. */
public class ManagedQueryMap<K, V> extends AbstractMap<K, V>
{
    private final ManagedAccess access;
    private final Function<Object, V> lookup;
    private final Supplier<Collection<K>> keys;

    public ManagedQueryMap(ManagedAccess access, Function<Object, V> lookup, Supplier<Collection<K>> keys)
    {
        this.access = access;
        this.lookup = lookup;
        this.keys = keys;
    }

    public V get(Object key)
    {
        access.check();
        return lookup.apply(key);
    }

    public boolean containsKey(Object key)
    {
        return get(key) != null;
    }

    public Set<Entry<K, V>> entrySet()
    {
        access.check();
        Set<Entry<K, V>> entries = Collections.newSetFromMap(new IdentityHashMap<>());
        for (K key : keys.get())
        {
            V value = lookup.apply(key);
            if (value != null)
            {
                entries.add(new SimpleImmutableEntry<>(key, value));
            }
        }
        return Collections.unmodifiableSet(entries);
    }
}
