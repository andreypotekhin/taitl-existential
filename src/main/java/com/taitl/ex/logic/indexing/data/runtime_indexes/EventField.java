package com.taitl.ex.logic.indexing.data.runtime_indexes;

/**
 * Maps event-type key to a set of event handlers configured
 * for event type + entity type as part of business op.
 * The data in this class is scoped to a single transaction.
 * Example: "Create<Doc<JSON>>" -> Set: OnCreate<Doc<JSON>>, OnCreate<Doc>
 */
public class EventField
{
    // TODO
    // public <T> Set<EventHandler<T>> get(EventKey<T> key)
    // {
    // }

    // TODO
    // public <T> Set<EventHandler<T>> put(EventKey<T> key, EventHandler<T> value)
    // {
    // }

}
