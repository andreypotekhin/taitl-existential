package com.taitl.ex.logic.execution.data;

import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.types.*;

import java.util.*;

/**
 * An immutable, array-backed sequence of handlers in execution order.
 * Construction copies the selected handlers and precomputes memo and bi-handler requirements,
 * so warm dispatch need not inspect handler kinds or sort declarations again.
 *
 * The selection describes reusable work and contains no reported entity or memo payload.
 * Appending a local selection preserves shared-before-local order and produces a new value;
 * only the owning transaction may retain a selection containing its local callback closures.
 */
public class RuleSelection
{
    protected List<Ev<?>> handlers;
    protected boolean memoRequired;
    protected boolean biHandler;

    public RuleSelection(Collection<? extends Ev<?>> handlers, boolean memoRequired)
    {
        Ev<?>[] values = handlers.toArray(new Ev<?>[0]);
        this.handlers = Collections.unmodifiableList(Arrays.asList(values));
        this.memoRequired = memoRequired;
        for (Ev<?> handler : values)
        {
            biHandler |= handler instanceof BiEventHandler<?>;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<Ev<T>> handlers()
    {
        return (List<Ev<T>>) (List<?>) handlers;
    }

    public boolean memoRequired()
    {
        return memoRequired;
    }

    public boolean biHandler()
    {
        return biHandler;
    }

    public RuleSelection append(RuleSelection local)
    {
        List<Ev<?>> merged = new ArrayList<>(handlers);
        merged.addAll(local.handlers);
        return new RuleSelection(merged, memoRequired || local.memoRequired);
    }
}
