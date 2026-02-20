package com.taitl.ex.logic.configuration.indexes.data;

/**
 * Maps event-type key to a set of event handlers configured
 * for event type + entity type as part of business op.
 * The data in this class is scoped to a single transaction.
 * Example: "Create<Doc<JSON>>" -> Set: OnCreate<Doc<JSON>>, OnCreate<Doc>
 * Example: "Create<Doc<JSON>>", "Update<Doc<HTML>>": rules (expressions, event handlers)
 * exist for these events, corresponding non-generic versions ("Create<Doc>").
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
    // TODO: add generic and elementary versions of the event key,
    // e.g. for "ReadAndLock<Doc<JSON>>" also add "ReadAndLock<Doc>", "ReadAndLock",
    // "Read<Doc<JSON>>", "Read<Doc>", "Change"
    // }

}
