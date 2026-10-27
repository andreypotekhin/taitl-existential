package com.taitl.ex.logic.managed_indexes.data;

import java.util.*;

/** Guards a physical identity group that is cleared in place on replacement and disposal. */
public class ManagedGroupSet<V> extends AbstractSet<V>
{
    private final ManagedAccess access;
    private final Set<V> values;
    private final Runnable refresh;

    public ManagedGroupSet(ManagedAccess access, Set<V> values, Runnable refresh)
    {
        this.access = access;
        this.values = values;
        this.refresh = refresh;
    }

    public int size()
    {
        access.check();
        refresh.run();
        return values.size();
    }

    public boolean contains(Object value)
    {
        access.check();
        refresh.run();
        return values.contains(value);
    }

    public Iterator<V> iterator()
    {
        access.check();
        refresh.run();
        Iterator<V> iterator = new ArrayList<>(values).iterator();
        return new Iterator<V>() {
            public boolean hasNext()
            {
                access.check();
                return iterator.hasNext();
            }

            public V next()
            {
                access.check();
                return iterator.next();
            }
        };
    }
}
