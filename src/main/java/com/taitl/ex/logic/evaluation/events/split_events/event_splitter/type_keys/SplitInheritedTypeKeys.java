package com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.existential.keys.*;

import java.lang.reflect.*;
import java.util.*;

public class SplitInheritedTypeKeys
{
    private static final ClassValue<Map<Boolean, Set<TypeKey<?>>>> HIERARCHY_TYPES =
            new ClassValue<Map<Boolean, Set<TypeKey<?>>>>() {
                protected Map<Boolean, Set<TypeKey<?>>> computeValue(Class<?> runtimeClass)
                {
                    SplitInheritedTypeKeys logic = new SplitInheritedTypeKeys();
                    Map<Boolean, Set<TypeKey<?>>> result = new HashMap<>();
                    result.put(false, logic.hierarchyTypes(runtimeClass, false));
                    result.put(true, logic.hierarchyTypes(runtimeClass, true));
                    return Collections.unmodifiableMap(result);
                }
            };

    @Logic
    protected SplitDeclaredTypeKey splitDeclaredTypeKey = Creator.create(SplitDeclaredTypeKey.class);

    @Logic
    protected AddUpperBoundTypeKeys addUpperBoundTypeKeys = Creator.create(AddUpperBoundTypeKeys.class);

    public Set<TypeKey<?>> call(Class<?> runtimeClass, boolean useFullName)
    {
        return HIERARCHY_TYPES.get(runtimeClass).get(useFullName);
    }

    protected Set<TypeKey<?>> hierarchyTypes(Class<?> runtimeClass, boolean useFullName)
    {
        Set<TypeKey<?>> result = new LinkedHashSet<>();
        collect(runtimeClass, Collections.emptyMap(), result, useFullName);
        return Collections.unmodifiableSet(result);
    }

    protected void collect(Type type, Map<TypeVariable<?>, Type> substitutions, Set<TypeKey<?>> result,
            boolean useFullName)
    {
        Type resolved = resolve(type, substitutions);
        Class<?> raw = rawClass(resolved);
        if (raw == null)
        {
            return;
        }

        Map<TypeVariable<?>, Type> bindings = bindings(resolved, raw, substitutions);
        if (!raw.equals(Object.class))
        {
            result.addAll(splitDeclaredTypeKey.call(TypeKey.valueOf(resolved, useFullName)));
            if (resolved instanceof ParameterizedType)
            {
                addUpperBoundTypeKeys.call((ParameterizedType) resolved, result, useFullName);
            }
        }
        Type parent = raw.getGenericSuperclass();
        if (parent != null)
        {
            collect(parent, bindings, result, useFullName);
        }
        for (Type implemented : raw.getGenericInterfaces())
        {
            collect(implemented, bindings, result, useFullName);
        }
    }

    protected Map<TypeVariable<?>, Type> bindings(Type resolved, Class<?> raw,
            Map<TypeVariable<?>, Type> substitutions)
    {
        Map<TypeVariable<?>, Type> bindings = new HashMap<>();
        if (resolved instanceof ParameterizedType)
        {
            TypeVariable<?>[] variables = raw.getTypeParameters();
            Type[] arguments = ((ParameterizedType) resolved).getActualTypeArguments();
            for (int i = 0; i < variables.length; i++)
            {
                bindings.put(variables[i], resolve(arguments[i], substitutions));
            }
        }
        return bindings;
    }

    protected Type resolve(Type type, Map<TypeVariable<?>, Type> substitutions)
    {
        if (type instanceof TypeVariable<?> && substitutions.containsKey(type))
        {
            return resolve(substitutions.get(type), substitutions);
        }
        if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterized = (ParameterizedType) type;
            Type[] arguments = parameterized.getActualTypeArguments();
            Type[] resolved = new Type[arguments.length];
            for (int i = 0; i < arguments.length; i++)
            {
                resolved[i] = resolve(arguments[i], substitutions);
            }
            return new ResolvedParameterizedType((Class<?>) parameterized.getRawType(), resolved,
                    parameterized.getOwnerType());
        }
        return type;
    }

    protected Class<?> rawClass(Type type)
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
