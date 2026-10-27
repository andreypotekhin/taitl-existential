package com.taitl.ex.logic.managed_indexes.data;

/** Validated candidate change; construction invokes extractors before any side is mutated. */
public class ManagedChange
{
    private final ManagedSide<Object, Object> side;
    private final Object before;
    private final Object after;
    private final Object key;

    public ManagedChange(ManagedSide<Object, Object> side, Object before, Object after)
    {
        this.side = side;
        this.before = before;
        this.after = after;
        key = after == null || side.deferred() ? null : side.extract(after);
    }

    public void apply()
    {
        if (side.deferred())
        {
            side.enroll(before, after);
            return;
        }
        if (before != null && before != after)
        {
            side.remove(before);
        }
        if (after != null)
        {
            side.accept(key, after);
        }
    }
}
