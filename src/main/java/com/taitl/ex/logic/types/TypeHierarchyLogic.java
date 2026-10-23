package com.taitl.ex.logic.types;

import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Resolves native superclass and interface signatures while substituting generic arguments.
 * Bindings propagate across hierarchy edges so a concrete child can expose parameterized
 * ancestor types. Duplicate resolved signatures are collected once in a read-only result.
 *
 * ClassValue associates reusable metadata with actual runtime Class identities without a static
 * map pinning application class loaders. Explicit Type resolution also supports upper-bound
 * matching. Neither path examines entity fields or infers arguments erased from the signature.
 */
public class TypeHierarchyLogic
{
    protected static ClassValue<Set<Type>> signatures = new ClassValue<Set<Type>>() {
        protected Set<Type> computeValue(Class<?> type)
        {
            return new TypeHierarchyLogic().resolve(type);
        }
    };

    public Set<Type> types(Class<?> type)
    {
        return signatures.get(type);
    }

    public Set<Type> resolve(Type type)
    {
        Map<String, Type> result = new LinkedHashMap<>();
        collect(type, Collections.emptyMap(), result);
        return Collections.unmodifiableSet(new LinkedHashSet<>(result.values()));
    }

    protected void collect(Type type, Map<TypeVariable<?>, Type> substitutions, Map<String, Type> result)
    {
        Type resolved = substitute(type, substitutions);
        Class<?> raw = rawClass(resolved);
        if (raw == null || result.putIfAbsent(resolved.getTypeName(), resolved) != null)
        {
            return;
        }
        Map<TypeVariable<?>, Type> bindings = new HashMap<>();
        if (resolved instanceof ParameterizedType)
        {
            TypeVariable<?>[] variables = raw.getTypeParameters();
            Type[] arguments = ((ParameterizedType) resolved).getActualTypeArguments();
            for (int i = 0; i < variables.length; i++)
            {
                bindings.put(variables[i], substitute(arguments[i], substitutions));
            }
        }
        Type parent = raw.getGenericSuperclass();
        if (parent != null)
        {
            collect(parent, bindings, result);
        }
        for (Type implemented : raw.getGenericInterfaces())
        {
            collect(implemented, bindings, result);
        }
    }

    protected Type substitute(Type type, Map<TypeVariable<?>, Type> substitutions)
    {
        if (type instanceof TypeVariable<?> && substitutions.containsKey(type) && substitutions.get(type) != type)
        {
            return substitute(substitutions.get(type), substitutions);
        }
        if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterized = (ParameterizedType) type;
            Type[] arguments = parameterized.getActualTypeArguments();
            Type[] resolved = new Type[arguments.length];
            for (int i = 0; i < arguments.length; i++)
            {
                resolved[i] = substitute(arguments[i], substitutions);
            }
            return new ResolvedParameterizedType((Class<?>) parameterized.getRawType(), resolved,
                    parameterized.getOwnerType());
        }
        return type;
    }

    public Class<?> rawClass(Type type)
    {
        if (type instanceof Class<?>)
        {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() instanceof Class<?>)
        {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return null;
    }
}
