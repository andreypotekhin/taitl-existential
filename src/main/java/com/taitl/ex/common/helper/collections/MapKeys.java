package com.taitl.ex.common.helper.collections;

import com.taitl.ex.common.helper.Args;

import java.util.Map;

public class MapKeys
{
    protected MapKeys()
    {
    }

    @SuppressWarnings("unchecked")
    public static <K> Class<? extends K> keyClass(Map<K, ?> storage, int size)
    {
        Args.sane(storage, "storage");
        if (size == 0)
        {
            throw new IllegalStateException("You can't call method getKeyClass() on an empty Multimap.");
        }
        K result = Coll.getFirst(storage.keySet());
        return (Class<? extends K>) result.getClass();
    }
}
