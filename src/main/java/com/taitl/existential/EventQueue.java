package com.taitl.existential;

import com.taitl.existential.event.base.Event;
import java.util.Set;
import java.util.ArrayList;

/**
 * Queues events (EntityEvent<T>, Mutation<T>, Transit<T>) for a duration of business transaction,
 * to make them available for handling/processing when transaction is about to finish.
 */
public class EventQueue<T> extends ArrayList<Set<Event<T>>>
{
    // TODO:
}
