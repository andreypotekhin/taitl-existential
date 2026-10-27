package com.taitl.ex.logic.managed_indexes.data;

import java.util.*;

/** Immutable raw-type routing shape for one binding; side contents remain transaction-owned. */
public class IndexMaintenancePlan
{
    private final Map<String, List<ManagedSide<Object, Object>>> mutations;
    private final Map<String, List<ManagedSide<Object, Object>>> reads;

    public IndexMaintenancePlan(Map<String, List<ManagedSide<Object, Object>>> mutations,
            Map<String, List<ManagedSide<Object, Object>>> reads)
    {
        this.mutations = freeze(mutations);
        this.reads = freeze(reads);
    }

    public Map<String, List<ManagedSide<Object, Object>>> routes(boolean read)
    {
        return read ? reads : mutations;
    }

    private Map<String, List<ManagedSide<Object, Object>>> freeze(
            Map<String, List<ManagedSide<Object, Object>>> source)
    {
        Map<String, List<ManagedSide<Object, Object>>> copy = new HashMap<>();
        source.forEach((key, value) -> copy.put(key, Collections.unmodifiableList(new ArrayList<>(value))));
        return Collections.unmodifiableMap(copy);
    }
}
