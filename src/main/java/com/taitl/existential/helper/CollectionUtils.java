package com.taitl.existential.helper;

import java.util.Collection;
import java.util.Optional;

public class CollectionUtils
{
    public static <T> T getFirst(Collection<T> coll)
    {
        Optional<T> result = coll.stream().findFirst();
        return result.isPresent() ? result.get() : null;
    }
}
