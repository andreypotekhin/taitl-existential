package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.existential.keys.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Caches immutable expanded descriptor sets for the legacy key-splitting entry point.
 * The key includes the descriptor text, actual descriptor/runtime Class identities, and naming
 * mode, preventing same-named types in different class loaders from sharing an entry.
 *
 * A fixed FIFO budget limits resident descriptors; hits bypass the insertion lock, while cold
 * resolution and publication are serialized. Keys use weak class references and values contain
 * rendered descriptors, allowing application classes to unload without waiting for FIFO eviction.
 * The compact EventField path performs its own lookup before this expansion is needed.
 */
public class TypeDescriptorCache
{
    public static final int CAPACITY = 4096;

    private final Map<TypeDescriptorKey, Set<TypeKey<?>>> entries = new ConcurrentHashMap<>();
    private final Queue<TypeDescriptorKey> insertionOrder = new ConcurrentLinkedQueue<>();
    private final Object insertionLock = new Object();

    @SuppressWarnings("unchecked")
    public <T> Set<TypeKey<T>> resolve(TypeKey<T> typeKey, Class<?> runtimeClass,
            boolean useFullName, Supplier<Set<TypeKey<T>>> resolver)
    {
        TypeDescriptorKey key = new TypeDescriptorKey(typeKey, runtimeClass, useFullName);
        Set<TypeKey<?>> cached = entries.get(key);
        if (cached != null)
        {
            return (Set<TypeKey<T>>) (Set<?>) cached;
        }
        synchronized (insertionLock)
        {
            cached = entries.get(key);
            if (cached != null)
            {
                return (Set<TypeKey<T>>) (Set<?>) cached;
            }
            Set<TypeKey<T>> resolved = Collections.unmodifiableSet(new LinkedHashSet<>(resolver.get()));
            entries.put(key, (Set<TypeKey<?>>) (Set<?>) resolved);
            insertionOrder.add(key);
            while (entries.size() > CAPACITY)
            {
                TypeDescriptorKey eldest = insertionOrder.poll();
                if (eldest != null)
                {
                    entries.remove(eldest);
                }
            }
            return resolved;
        }
    }

    public int size()
    {
        return entries.size();
    }
}
