package com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys;

import com.taitl.existential.keys.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Adds rendered upper-bound variants for the legacy expanded-key interface.
 * For each concrete class argument, superclass and interface bounds replace that argument
 * one position at a time. Object is omitted, and nested parameterized arguments are left intact.
 *
 * These variants preserve existing descriptor-splitting behavior. The compact selection path
 * uses captured structural signatures to check simultaneous bounds independently; it does not
 * rely on this adapter to enumerate wildcard combinations.
 */
public class AddUpperBoundTypeKeys
{
    public void call(ParameterizedType type, Set<TypeKey<?>> result, boolean useFullName)
    {
        Type[] arguments = type.getActualTypeArguments();
        for (int i = 0; i < arguments.length; i++)
        {
            if (!(arguments[i] instanceof Class<?>))
            {
                continue;
            }
            Set<Class<?>> bounds = new LinkedHashSet<>();
            collectBounds((Class<?>) arguments[i], bounds);
            bounds.remove(Object.class);
            for (Class<?> bound : bounds)
            {
                Type[] bounded = arguments.clone();
                bounded[i] = new ResolvedWildcardType(bound, useFullName);
                ParameterizedType keyType = new ResolvedParameterizedType((Class<?>) type.getRawType(), bounded,
                        type.getOwnerType());
                result.add(TypeKey.valueOf(keyType, useFullName));
            }
        }
    }

    protected void collectBounds(Class<?> type, Set<Class<?>> bounds)
    {
        if (!bounds.add(type))
        {
            return;
        }
        Class<?> parent = type.getSuperclass();
        if (parent != null)
        {
            collectBounds(parent, bounds);
        }
        for (Class<?> implemented : type.getInterfaces())
        {
            collectBounds(implemented, bounds);
        }
    }
}
