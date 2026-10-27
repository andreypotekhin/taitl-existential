package com.taitl.ex.cross.caching;

import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Thread-safe cache for runtime-inferred type keys.
 * This avoids repeated TypeKey construction on hot event paths when the same runtime classes
 * recur across events. ClassValue associations avoid an unbounded global map rooting application classes.
 */
public class TypeKeyCache
{
    protected final ClassValue<TypeKey<?>> shortNames = names(false);
    protected final ClassValue<TypeKey<?>> fullNames = names(true);

    protected ClassValue<TypeKey<?>> names(boolean full)
    {
        return new ClassValue<TypeKey<?>>() {
            protected TypeKey<?> computeValue(Class<?> type)
            {
                return new TypeKey<>(type, full);
            }
        };
    }

    public <T> TypeKey<T> get(T t, boolean useFullName)
    {
        sane(t, "t");
        return get(t.getClass(), useFullName);
    }

    public <T> TypeKey<T> get(Class<?> clz, boolean useFullName)
    {
        sane(clz, "clz");
        ClassValue<TypeKey<?>> cache = useFullName ? fullNames : shortNames;
        return cast(cache.get(clz));
    }

    @SuppressWarnings("unchecked")
    protected static <T> TypeKey<T> cast(TypeKey<?> typeKey)
    {
        return (TypeKey<T>) typeKey;
    }
}
