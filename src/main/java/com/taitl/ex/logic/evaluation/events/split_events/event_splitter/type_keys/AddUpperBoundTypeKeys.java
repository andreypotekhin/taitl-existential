package com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys;

import com.taitl.existential.keys.*;

import java.lang.reflect.*;
import java.util.*;

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
