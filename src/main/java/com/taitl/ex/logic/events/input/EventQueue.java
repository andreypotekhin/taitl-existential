package com.taitl.ex.logic.events.input;

import java.util.*;
import com.taitl.existential.events.types.*;

/**
 * Queues events (EntityEvent<T>, Mutation<T>, Transit<T>) for a duration of business transaction,
 * to make them available for handling/processing when transaction is about to finish.
 */
// See suggestion S02182601 for performance improvement
public class EventQueue<T> extends ArrayList<Set<Event<T>>>
{
    // TODO:
}
