package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.common.helper.lang.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Splits a type key into a set of less-generic type keys, by reducing generic parameter detail.
 *
 * Examples:
 * <pre>
 * T<A<X>> -> T<A<X>>, T<A<?>>, T<A>
 * T<A<X>,B<Y>> -> T<A<X>,B<Y>>, T<A<?>,B<Y>>, T<A<X>,B<Y>>, T<A<X>,B<?>>, T<A<X>,B>, T<A<?>,B<Y>>, T<A<?>,B<?>>, T<A<?>,B>, , T<A,B<Y>>, T<A,B<?>>, T<A,B>
 * </pre>
 */
public class SplitTypeKey
{
    @Logic
    protected static GenericsVariants genericsVariants = Creator.create(GenericsVariants.class);

    public String root(String key)
    {
        sane(key, "key");
        int left = key.indexOf('<');
        return (left > -1 ? key.substring(0, left) : key).trim();
    }

    public <T> Set<TypeKey<T>> split(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        String key = typeKey.toString();
        int left = key.indexOf('<');
        boolean hasGenerics = left > -1;
        String root = root(key);
        Set<TypeKey<T>> result = new LinkedHashSet<>();
        for (String variant : genericsVariants.call(key))
        {
            if (hasGenerics && root.equals(variant))
            {
                continue;
            }
            result.add(TypeKey.valueOf(variant));
        }
        return result;
    }
}
