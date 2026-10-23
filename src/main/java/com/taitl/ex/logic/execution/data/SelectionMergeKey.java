package com.taitl.ex.logic.execution.data;

/**
 * Identifies a merged shared/local selection for one dispatch shape.
 * The signature uses value equality, while the two selection references use identity to include
 * their source generations. Recompiled local handlers cannot reuse an older merged result.
 *
 * Neither hashing nor comparison invokes callback equality or user-defined callback hashCode.
 * The key belongs only to a bounded transaction-local overlay, keeping local closures outside
 * shared operation caches.
 */
public class SelectionMergeKey
{
    protected Object signature;
    protected Object base;
    protected Object local;

    public SelectionMergeKey(Object signature, Object base, Object local)
    {
        this.signature = signature;
        this.base = base;
        this.local = local;
    }

    public int hashCode()
    {
        return 31 * (31 * signature.hashCode() + System.identityHashCode(base)) + System.identityHashCode(local);
    }

    public boolean equals(Object other)
    {
        if (!(other instanceof SelectionMergeKey))
        {
            return false;
        }
        SelectionMergeKey key = (SelectionMergeKey) other;
        return base == key.base && local == key.local && signature.equals(key.signature);
    }
}
