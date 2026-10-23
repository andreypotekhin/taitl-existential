package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.existential.keys.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/** Keeps a bounded set of resolved descriptors used during event dispatch. */
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
