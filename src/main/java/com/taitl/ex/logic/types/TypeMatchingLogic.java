package com.taitl.ex.logic.types;

import com.taitl.ex.cross.logging.*;
import com.taitl.existential.keys.*;

import java.lang.reflect.*;
import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class TypeMatchingLogic
{
    private static final int WARNING_CAPACITY = 64;
    private final Map<Class<?>, Set<String>> warned = new WeakHashMap<>();

    public void validateRuleType(TypeKey<?> typeKey)
    {
        sane(typeKey, "typeKey");
        if (typeKey.toString().contains("? super "))
        {
            throw new IllegalArgumentException(String.format(
                    "Lower-bounded rule type '%s' is not supported. Use an upper bound or an unbounded wildcard. "
                            + "See /Troubleshooting.md#unsupported-lower-bounded-type-rules",
                    typeKey));
        }
    }

    public void warnUnresolvedGenerics(String op, TypeKey<?> typeKey, Class<?> runtimeClass, boolean enabled)
    {
        sane(op, "op", typeKey, "typeKey", runtimeClass, "runtimeClass");
        if (!enabled || typeKey.toString().contains("<") || !hasUnresolvedGenerics(runtimeClass))
        {
            return;
        }
        String warningKey = op + "|" + typeKey;
        synchronized (warned)
        {
            Set<String> warnings = warned.computeIfAbsent(runtimeClass, key -> new LinkedHashSet<>());
            if (warnings.contains(warningKey))
            {
                return;
            }
            if (warnings.size() == WARNING_CAPACITY)
            {
                warnings.remove(warnings.iterator().next());
            }
            warnings.add(warningKey);
        }
        if (Log.isWarnEnabled())
        {
            Log.warn(TypeMatchingLogic.class, null,
                    String.format("Operation '%s' reports runtime type '%s' with unresolved generic parameters. "
                            + "Report it with an explicit "
                            + "TypeKey to select rules safely. See /Troubleshooting.md#unresolved-generic-types",
                            op, runtimeClass.getName()),
                    "operation", op, "runtimeType", runtimeClass.getName(), "typeKey", typeKey.toString());
        }
    }

    public boolean hasUnresolvedGenerics(Class<?> runtimeClass)
    {
        sane(runtimeClass, "runtimeClass");
        if (runtimeClass.getTypeParameters().length > 0)
        {
            return true;
        }
        Type parent = runtimeClass.getGenericSuperclass();
        if (unresolved(parent))
        {
            return true;
        }
        for (Type contract : runtimeClass.getGenericInterfaces())
        {
            if (unresolved(contract))
            {
                return true;
            }
        }
        return false;
    }

    protected boolean unresolved(Type type)
    {
        if (type instanceof TypeVariable<?>)
        {
            return true;
        }
        if (type instanceof ParameterizedType)
        {
            for (Type argument : ((ParameterizedType) type).getActualTypeArguments())
            {
                if (unresolved(argument))
                {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof WildcardType)
        {
            for (Type bound : ((WildcardType) type).getUpperBounds())
            {
                if (unresolved(bound))
                {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof GenericArrayType)
        {
            return unresolved(((GenericArrayType) type).getGenericComponentType());
        }
        return type instanceof Class<?> && ((Class<?>) type).getTypeParameters().length > 0;
    }
}
