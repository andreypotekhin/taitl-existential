package com.taitl.exlogic.events.queue;

import java.util.Set;

import com.taitl.existential.events.types.Event;

import java.util.ArrayList;

/**
 * Queues events (EntityEvent<T>, Mutation<T>, Transit<T>) for a duration of business transaction,
 * to make them available for handling/processing when transaction is about to finish.
 */
public class EventQueue<T> extends ArrayList<Set<Event<T>>>
{
    // TODO:
}
