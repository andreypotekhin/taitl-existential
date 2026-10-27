package com.taitl.ex.logic.types;

import com.taitl.ex.cross.logging.*;
import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.types.data.*;
import com.taitl.existential.keys.*;

import java.lang.reflect.*;
import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Coordinates structural type matching and diagnostics for unsupported or erased types.
 * Compatibility delegates to MatchTypePattern; configuration rejects lower-bounded rules with
 * an actionable remedy, while reporting can warn when a generic runtime class lacks an explicit
 * TypeKey. Warning enablement is supplied by the caller's resolved configuration.
 *
 * Reflection diagnostics are cached per Class with ClassValue. A bounded per-instance cache
 * suppresses repeated warning signatures and is cleared on library close, avoiding an
 * unbounded operation-name or application-class registry.
 */
public class TypeMatchingLogic
{
    protected static ClassValue<Boolean> unresolvedTypes = new ClassValue<Boolean>() {
        protected Boolean computeValue(Class<?> type)
        {
            return unresolvedClass(type);
        }
    };

    @Logic
    protected MatchTypePattern matchTypePattern = Creator.create(MatchTypePattern.class);

    private static final int WARNING_CAPACITY = 64;
    protected BoundedCache<List<?>, Boolean> warned = new BoundedCache<>(WARNING_CAPACITY);

    public boolean matches(TypePattern configured, TypePattern reported)
    {
        return matchTypePattern.call(configured, reported);
    }

    public void validateRuleType(TypeKey<?> typeKey)
    {
        sane(typeKey, "typeKey");
        if (typeKey.toString().contains("? super "))
        {
            throw com.taitl.ex.logic.diagnostics.OperationalDiagnostics.argument(String.format(
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
        List<?> warningKey = Arrays.asList(runtimeClass, op, typeKey.toString());
        synchronized (warned)
        {
            if (warned.get(warningKey) != null)
            {
                return;
            }
            warned.put(warningKey, true);
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

    public void close()
    {
        warned.clear();
    }

    public boolean hasUnresolvedGenerics(Class<?> runtimeClass)
    {
        sane(runtimeClass, "runtimeClass");
        return unresolvedTypes.get(runtimeClass);
    }

    protected static boolean unresolvedClass(Class<?> runtimeClass)
    {
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

    protected static boolean unresolved(Type type)
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
