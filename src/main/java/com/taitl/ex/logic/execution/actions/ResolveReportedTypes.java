package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.types.*;
import com.taitl.ex.logic.types.data.*;
import com.taitl.existential.keys.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Collects structural subject patterns for a cold dispatch lookup.
 * The declared TypeKey remains authoritative; captured native type metadata is attached only
 * when its rendered short or full name agrees with that key. Runtime class signatures add
 * provable superclass and interface types through TypeHierarchyLogic.
 *
 * Captured argument bounds remain independent facts, avoiding a Cartesian product of wildcard
 * strings. Erased or symbolic names never trigger class loading or guessed generic arguments.
 * The result retains type metadata without retaining the reported entity.
 */
public class ResolveReportedTypes
{
    @Logic
    protected TypeHierarchyLogic hierarchy = Creator.create(TypeHierarchyLogic.class);

    public Set<TypePattern> call(RuntimeKey<?> key, boolean fullNames)
    {
        Set<TypePattern> result = new LinkedHashSet<>();
        Type captured = null;
        Type parent = key.typeKey().getClass().getGenericSuperclass();
        if (parent instanceof ParameterizedType && ((ParameterizedType) parent).getRawType().equals(TypeKey.class))
        {
            captured = ((ParameterizedType) parent).getActualTypeArguments()[0];
        }
        result.add(pattern(key.typeKey().toString(), captured));
        if (key.entity() != null)
        {
            for (Type type : hierarchy.types(key.entity().getClass()))
            {
                if (!type.equals(Object.class))
                {
                    result.add(pattern(TypeKey.valueOf(type, fullNames).toString(), type));
                }
            }
        }
        return result;
    }

    protected TypePattern pattern(String descriptor, Type type)
    {
        return new TypePattern(descriptor, type != null && describes(descriptor, type) ? type : null);
    }

    protected boolean describes(String descriptor, Type type)
    {
        return descriptor.equals(TypeKey.valueOf(type, false).toString())
                || descriptor.equals(TypeKey.valueOf(type, true).toString());
    }

}
