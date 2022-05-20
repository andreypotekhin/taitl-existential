package com.taitl.existential.utilities;

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
