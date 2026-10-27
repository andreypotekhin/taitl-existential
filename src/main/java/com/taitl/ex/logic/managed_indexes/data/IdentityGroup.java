package com.taitl.ex.logic.managed_indexes.data;

import java.util.*;

/** Identity membership whose cleared owner releases capacity even through an escaped group view. */
public class IdentityGroup<V> extends AbstractSet<V>
{
    private Map<V, Boolean> values = new IdentityHashMap<>();

    public boolean add(V value)
    {
        return values.put(value, true) == null;
    }

    public boolean contains(Object value)
    {
        return values.containsKey(value);
    }

    public boolean remove(Object value)
    {
        boolean removed = values.remove(value) != null;
        if (values.isEmpty())
        {
            values = new IdentityHashMap<>();
        }
        return removed;
    }

    public int size()
    {
        return values.size();
    }

    public Iterator<V> iterator()
    {
        return values.keySet().iterator();
    }

    public void clear()
    {
        values.clear();
        values = new IdentityHashMap<>();
    }
}
