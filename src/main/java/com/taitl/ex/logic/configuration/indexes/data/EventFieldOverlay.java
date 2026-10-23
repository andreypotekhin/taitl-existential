package com.taitl.ex.logic.configuration.indexes.data;

import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Combines shared operation selections with a transaction's prepared local handlers.
 * Each source resolves its own generation, then a bounded cache retains the merged immutable
 * selection in shared-before-local order. Selection identities in the merge key prevent reuse
 * after local declarations change.
 *
 * The overlay belongs to the transaction because merged selections may contain local closures.
 * Closing it releases its merged cache without closing or mutating the shared base field;
 * the owning stage separately disposes its local indexes.
 */
public class EventFieldOverlay extends EventField
{
    protected EventField base;
    protected EventField overlay;
    protected BoundedCache<SelectionMergeKey, RuleSelection> merged = new BoundedCache<>(PreparedSelections.CAPACITY);

    public EventFieldOverlay(EventField base, EventField overlay)
    {
        super(base.ci, base.source());
        sane(base, "base", overlay, "overlay");
        this.base = base;
        this.overlay = overlay;
    }

    public RuleSelection select(DispatchSignature signature, Supplier<SelectionInput> input)
    {
        RuleSelection shared = base.select(signature, input);
        RuleSelection local = overlay.select(signature, input);
        return merged.resolve(new SelectionMergeKey(signature, shared, local), () -> shared.append(local));
    }

    public <T> List<Ev<T>> get(MultiKey<T> multiKey)
    {
        List<Ev<T>> shared = base.get(multiKey);
        List<Ev<T>> local = overlay.get(multiKey);
        SelectionMergeKey key = new SelectionMergeKey(multiKey.toString(), shared, local);
        return merged.resolve(key, () -> new RuleSelection(shared, false).append(new RuleSelection(local, false)))
                .handlers();
    }

    public <T> boolean hasBiEventKey(EventKey<T> eventKey)
    {
        return base.hasBiEventKey(eventKey) || overlay.hasBiEventKey(eventKey);
    }

    public int cachedSelections()
    {
        return merged.size();
    }

    public void close()
    {
        merged.clear();
        super.close();
    }
}
