package com.taitl.ex.logic.configuration.indexes.data;

import com.taitl.ex.logic.events.logic.EventSplitter;
import com.taitl.existential.keys.EventKey;
import com.taitl.existential.keys.MultiKey;

/**
 * Maps event keys to a set of event handlers configured
 * for event type + entity type as part of business op.
 * The data in this class is scoped to a single business operation.
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
    // TODO: cache previously returned results

    // TODO
    // public <T> List<EventHandler<T>> get(EventKey<T> key)
    // {
    // }

    // TODO
    // public <T> List<EventHandler<T>> put(EventKey<T> key, EventHandler<T> value)
    // {
    // TODO: add generic and elementary versions of the event key,
    // e.g. for "ReadAndLock<Doc<JSON>>" also add "ReadAndLock<Doc>", "ReadAndLock",
    // "Read<Doc<JSON>>", "Read<Doc>", "Change"
    // }

}
