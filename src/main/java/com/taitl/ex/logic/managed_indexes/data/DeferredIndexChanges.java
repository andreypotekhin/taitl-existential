package com.taitl.ex.logic.managed_indexes.data;

import java.util.*;
import java.util.function.*;

/**
 * Folds multi-valued enrollment by object identity without evaluating keys on every report.
 * All keys are validated before the compact pending batch is applied. Single-valued indexes
 * instead capture keys when reported because historical collisions can destructively replace
 * entries even when no query observes the intermediate state.
 */
public class DeferredIndexChanges<K, V>
{
    private Map<V, Boolean> pending = new IdentityHashMap<>();
    private boolean flushing;

    public void enroll(V before, V after, ManagedMembership<K, V> membership)
    {
        if (before != null && before != after)
        {
            if (membership.key(before) == null)
            {
                pending.remove(before);
            }
            else
            {
                pending.put(before, false);
            }
        }
        if (after != null)
        {
            pending.put(after, true);
        }
    }

    public void flush(ManagedMembership<K, V> membership, Function<V, K> extractor)
    {
        if (pending.isEmpty())
        {
            return;
        }
        if (flushing)
        {
            throw new IllegalStateException("Key extractors must not query or mutate their own managed index");
        }
        flushing = true;
        try
        {
            Map<V, K> keys = new IdentityHashMap<>();
            pending.forEach((value, enrolled) -> {
                if (enrolled)
                {
                    keys.put(value, extractor.apply(value));
                }
            });
            pending.forEach((value, enrolled) -> {
                if (enrolled)
                {
                    membership.add(keys.get(value), value);
                }
                else
                {
                    membership.remove(value);
                }
            });
            pending.clear();
        }
        finally
        {
            flushing = false;
        }
    }

    public int size()
    {
        return pending.size();
    }

    public void close()
    {
        pending.clear();
        pending = new IdentityHashMap<>();
    }
}
