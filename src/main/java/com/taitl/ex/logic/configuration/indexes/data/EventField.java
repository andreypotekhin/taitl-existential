package com.taitl.ex.logic.configuration.indexes.data;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Maps event key/event keys (a MultiKey) to a list of configured event handlers.
 * For a MultiKey (e.g. "Create<Doc<JSON>>,Create<Doc<?>>,Create<Doc>")
 * returns a list of event handlers in the order of their declaration.
 * This class scope is a single business operation.
 *
 * EventField implementation is 'lazy': it retrieves and sorts handlers for a MultiKey
 * on demand and caches the result for subsequent retrievals.
 *
 * Example:
 * Input: "Create<Doc<JSON>>,Create<Doc<?>>,Create<Doc>"
 * Output: List<EventHandler<T>>: OnCreate<Doc<JSON>>, OnCreate<Doc<?>>, OnCreate<Doc> etc.
 * - list of rules (expressions, event handlers) configured for these events.
 * - the order of rules in the list follows their declaration order.
 * - the returned value is cached to speed up subsequent retrievals.
 *
 * Called by EvaluationLogic.
 *
 * @see MultiKey
 * @see EventKey
 * @see EvaluationLogic
 */
public class EventField
{
    @Up
    protected ConfigurationIndexes ci;

    @Logic
    protected ConfiguredHandlers configured;

    /** MultiKey -> List<Ev<T>> */
    protected ListMap<String, Ev<?>> map = new ListMap<>();

    public EventField(ConfigurationIndexes ci)
    {
        this(ci, ci != null ? ci.configuredHandlers : null);
    }

    public EventField(ConfigurationIndexes ci, ConfiguredHandlers configured)
    {
        this.ci = ci;
        sane(configured, "configured");
        this.configured = configured;
    }

    protected ConfiguredHandlers source()
    {
        return configured;
    }

    /**
     * For the given multiKey, retrieves handlers from configuredHandlers index,
     * sorts them by their declaration order and returns as a list of Ev<?>.
     * Caches the result for subsequent retrievals.
     */
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
        ConfiguredHandlers configured = source();
        verify(configured.ready(), "Configured handlers index is not ready");
        List<OrderlyEv<?>> handlers = new ArrayList<>();
        for (EventKey<T> eventKey : multiKey.eventKeys())
        {
            Set<OrderlyEv<?>> set = configured.get(eventKey);
            if (set != null)
            {
                handlers.addAll(set);
            }
        }
        ci.maintainGlobalOrder.sort(handlers);
        cached = new LinkedList<>();
        for (OrderlyEv<?> handler : handlers)
        {
            cached.add(handler.ev());
        }
        synchronized (this)
        {
            map.putList(key, cached);
        }
        return (List<Ev<T>>) (List<?>) cached;
    }

    public <T> boolean hasBiEventKey(EventKey<T> eventKey)
    {
        sane(eventKey, "eventKey");
        return ci.hasBiKey(eventKey);
    }
}
