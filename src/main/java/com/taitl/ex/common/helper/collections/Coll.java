package com.taitl.ex.common.helper.collections;

import java.util.*;

public class Coll
{
    public static <T> T getFirst(Collection<T> coll)
    {
        Optional<T> result = coll.stream().findFirst();
        return result.isPresent() ? result.get() : null;
    }
}
