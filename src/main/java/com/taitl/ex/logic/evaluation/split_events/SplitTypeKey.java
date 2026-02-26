package com.taitl.ex.logic.evaluation.split_events;

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
    protected final GenericsVariants genericsVariants = new GenericsVariants();

    public <T> Set<TypeKey<T>> split(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        String key = typeKey.toString();
        int left = key.indexOf('<');
        boolean hasGenerics = left > -1;
        String root = hasGenerics ? key.substring(0, left).trim() : key.trim();
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
