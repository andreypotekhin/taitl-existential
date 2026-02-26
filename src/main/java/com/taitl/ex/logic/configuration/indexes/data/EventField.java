package com.taitl.ex.logic.configuration.indexes.data;

import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.evaluation.split_events.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Maps event key/event keys (a MultiKey) to a set of configured event handlers.
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
 * - list order follows rules' declaration order.
 * - Returned values (lists) are cached to speed up repeat retrievals.
 *
 * @see MultiKey
 * @see EventKey
 * @see EventSplitter
 */
public class EventField
{
    protected ListMap<MultiKey, Ev<?>> map = new ListMap<>();
    protected ConfigurationIndexes ci;

    public EventField(ConfigurationIndexes ci)
    {
        this.ci = ci;
    }

    /**
     * For the given multiKey, retrieves handlers from configuredHandlers index,
     * sorts them by their declaration order and returns as a list of Ev<?>.
     * Caches the result for subsequent retrievals.
     */
    public List<Ev<?>> get(MultiKey multiKey)
    {
        sane(multiKey, "multiKey");
        List<Ev<?>> cached = map.get(multiKey);
        if (cached != null)
        {
            return cached;
        }
        verify(ci.configuredHandlers.ready(), "Configured handlers index is not ready");
        List<OrderlyEv<?>> handlers = new ArrayList<>();
        for (EventKey eventKey : multiKey.eventKeys())
        {
            Set<OrderlyEv<?>> set = ci.configuredHandlers.get(eventKey);
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
            map.putList(multiKey, cached);
        }
        return cached;
    }
}
