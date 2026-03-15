package com.taitl.ex.logic.transactions.data;

import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Transaction-local before-state snapshots keyed by object identity and type key.
 */
public class MemoData
{
    public static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#memo-state-missing";

    /** TypeLey -> entity ID (object identity) -> entity before-state snapshot */
    protected Map<TypeKey<?>, Map<Object, Object>> memos = new LinkedHashMap<>();

    public <T> void put(T live, T before, TypeKey<T> typeKey) throws MemoException
    {
        sane(live, "live", before, "before", typeKey, "typeKey");
        if (live == before)
        {
            throw new MemoException("Argument 'before' must be a detached snapshot, not the same instance as 'live'. "
                    + "Use clone(), copy construction, or another snapshot strategy. See "
                    + TROUBLESHOOTING_SECTION);
        }
        if (contains(live, typeKey))
        {
            throw new MemoException("Memo state is already registered for this entity and type key. "
                    + "Use the first registered before-state for the transaction. See "
                    + TROUBLESHOOTING_SECTION);
        }
        byType(typeKey).put(live, before);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(T live, TypeKey<T> typeKey)
    {
        sane(live, "live", typeKey, "typeKey");
        Map<Object, Object> typed = memos.get(typeKey);
        if (typed == null)
        {
            return null;
        }
        return (T) typed.get(live);
    }

    public <T> boolean contains(T live, TypeKey<T> typeKey)
    {
        sane(live, "live", typeKey, "typeKey");
        Map<Object, Object> typed = memos.get(typeKey);
        return typed != null && typed.containsKey(live);
    }

    public void clear()
    {
        memos.clear();
    }

    protected <T> Map<Object, Object> byType(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        return memos.computeIfAbsent(typeKey, key -> new IdentityHashMap<>());
    }
}
