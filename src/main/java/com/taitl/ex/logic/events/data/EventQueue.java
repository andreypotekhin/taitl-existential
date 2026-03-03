package com.taitl.ex.logic.events.data;

import com.taitl.existential.events.types.*;

import java.util.*;

/**
 * Queues events (EntityEvent<T>, Transition<T>, Port<T>) for a duration of business transaction,
 * to make them available for handling/processing when transaction is about to finish.
 */
// See suggestion S02182601 for performance improvement
public class EventQueue<T> extends ArrayList<Set<Event<T>>>
{
    // TODO:
}
