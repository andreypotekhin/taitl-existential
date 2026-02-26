package com.taitl.ex.cross.caching;

import java.util.*;
import java.util.concurrent.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Thread-safe cache for runtime-inferred type keys.
 * <p>
 * This avoids repeated TypeKey construction on hot event paths when the same runtime classes
 * recur across events.
 */
public class TypeKeyCache
{
    protected final Map<Class<?>, TypeKey<?>> shortNames = new ConcurrentHashMap<>();
    protected final Map<Class<?>, TypeKey<?>> fullNames = new ConcurrentHashMap<>();

    public <T> TypeKey<T> get(T t, boolean useFullName)
    {
        sane(t, "t");
        return get(t.getClass(), useFullName);
    }

    public <T> TypeKey<T> get(Class<?> clz, boolean useFullName)
    {
        sane(clz, "clz");
        Map<Class<?>, TypeKey<?>> cache = useFullName ? fullNames : shortNames;
        return cast(cache.computeIfAbsent(clz, c -> new TypeKey<>(c, useFullName)));
    }

    @SuppressWarnings("unchecked")
    protected static <T> TypeKey<T> cast(TypeKey<?> typeKey)
    {
        return (TypeKey<T>) typeKey;
    }
}
