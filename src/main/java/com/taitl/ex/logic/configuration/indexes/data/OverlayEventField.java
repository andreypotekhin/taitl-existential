package com.taitl.ex.logic.configuration.indexes.data;

import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Caches a merged view of static and transaction-local event handlers.
 */
public class OverlayEventField extends EventField
{
    protected final EventField base;
    protected final EventField overlay;

    public OverlayEventField(EventField base, EventField overlay)
    {
        super(base.ci, base.source());
        sane(base, "base", overlay, "overlay");
        this.base = base;
        this.overlay = overlay;
    }

    @SuppressWarnings("unchecked")
    public <T> List<Ev<T>> get(MultiKey<T> multiKey)
    {
        sane(multiKey, "multiKey");
        String key = multiKey.toString();
        List<Ev<?>> cached = map.get(key);
        if (cached != null)
        {
            return (List<Ev<T>>) (List<?>) cached;
        }

        List<Ev<T>> baseHandlers = base.get(multiKey);
        List<Ev<T>> overlayHandlers = overlay.get(multiKey);
        List<Ev<T>> merged = merge(baseHandlers, overlayHandlers);
        synchronized (this)
        {
            map.putList(key, (List<Ev<?>>) (List<?>) merged);
        }
        return (List<Ev<T>>) (List<?>) map.get(key);
    }

    public <T> boolean hasBiEventKey(EventKey<T> eventKey)
    {
        sane(eventKey, "eventKey");
        return base.hasBiEventKey(eventKey) || overlay.hasBiEventKey(eventKey);
    }

    protected <T> List<Ev<T>> merge(List<Ev<T>> baseHandlers, List<Ev<T>> overlayHandlers)
    {
        sane(baseHandlers, "baseHandlers", overlayHandlers, "overlayHandlers");
        if (baseHandlers.isEmpty())
        {
            return overlayHandlers;
        }
        if (overlayHandlers.isEmpty())
        {
            return baseHandlers;
        }

        List<Ev<T>> merged = new LinkedList<>();
        merged.addAll(baseHandlers);
        merged.addAll(overlayHandlers);
        return merged;
    }
}
