package com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.common.helper.lang.*;
import com.taitl.existential.keys.*;

import java.lang.reflect.*;
import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Expands a declared TypeKey for callers of the legacy split-key interface.
 * Rendered generic variants and captured upper bounds become separate lookup descriptors;
 * native metadata is available when an anonymous TypeKey subclass captures a parameterized type.
 *
 * The compact selection path instead checks structural arguments independently and does not
 * use this variant expansion. Keeping this adapter preserves existing expanded-key callers
 * without imposing their allocation cost on warm EventField dispatch.
 */
public class SplitDeclaredTypeKey
{
    @Logic
    protected GenericsVariants genericsVariants = Creator.create(GenericsVariants.class);

    @Logic
    protected AddUpperBoundTypeKeys addUpperBoundTypeKeys = Creator.create(AddUpperBoundTypeKeys.class);

    public <T> Set<TypeKey<T>> call(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        Set<TypeKey<T>> result = new LinkedHashSet<>();
        for (String variant : genericsVariants.call(typeKey.toString()))
        {
            result.add(TypeKey.valueOf(variant));
        }
        Type captured = capturedType(typeKey);
        if (captured instanceof ParameterizedType)
        {
            Set<TypeKey<?>> bounds = new LinkedHashSet<>();
            addUpperBoundTypeKeys.call((ParameterizedType) captured, bounds, false);
            for (TypeKey<?> bound : bounds)
            {
                result.add(cast(bound));
            }
        }
        return result;
    }

    protected Type capturedType(TypeKey<?> typeKey)
    {
        Type parent = typeKey.getClass().getGenericSuperclass();
        if (parent instanceof ParameterizedType && ((ParameterizedType) parent).getRawType().equals(TypeKey.class))
        {
            return ((ParameterizedType) parent).getActualTypeArguments()[0];
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected static <T> TypeKey<T> cast(TypeKey<?> key)
    {
        return (TypeKey<T>) key;
    }
}
