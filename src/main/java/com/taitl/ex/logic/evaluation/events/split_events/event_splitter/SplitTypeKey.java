package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/** Splits reported type keys into the matching generic and inherited type keys. */
public class SplitTypeKey
{
    @Logic
    protected SplitDeclaredTypeKey splitDeclaredTypeKey = Creator.create(SplitDeclaredTypeKey.class);

    @Logic
    protected SplitInheritedTypeKeys splitInheritedTypeKeys = Creator.create(SplitInheritedTypeKeys.class);

    @Logic
    protected static TypeDescriptorCache descriptorCache = Creator.create(TypeDescriptorCache.class);

    public String root(String key)
    {
        sane(key, "key");
        int left = key.indexOf('<');
        return (left > -1 ? key.substring(0, left) : key).trim();
    }

    public <T> Set<TypeKey<T>> split(TypeKey<T> typeKey)
    {
        return splitDeclaredTypeKey.call(typeKey);
    }

    public <T> Set<TypeKey<T>> split(TypeKey<T> typeKey, Class<?> runtimeClass, boolean useFullName)
    {
        sane(typeKey, "typeKey", runtimeClass, "runtimeClass");
        return descriptorCache.resolve(typeKey, runtimeClass, useFullName,
                () -> resolve(typeKey, runtimeClass, useFullName));
    }

    protected <T> Set<TypeKey<T>> resolve(TypeKey<T> typeKey, Class<?> runtimeClass, boolean useFullName)
    {
        Set<TypeKey<T>> result = new LinkedHashSet<>(split(typeKey));
        for (TypeKey<?> key : splitInheritedTypeKeys.call(runtimeClass, useFullName))
        {
            result.add(cast(key));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected static <T> TypeKey<T> cast(TypeKey<?> key)
    {
        return (TypeKey<T>) key;
    }
}
