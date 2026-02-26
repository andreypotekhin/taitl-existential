package com.taitl.existential.keys;

import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.logic.*;

import java.util.*;
import java.util.stream.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Combines several event keys {@link EventKey} into a single key.
 * The event keys usually relate to a single event that has been split
 * into elementary events by EventSplitter.
 * MultiKey for accessing the EventField.
 *
 * Example:
 * Event keys: "Create<Doc<JSON>>", "Create<Doc<?>>", "Create<Doc>"
 * MultiKey:  "Create<Doc<JSON>>,Create<Doc<?>>,Create<Doc>"
 *
 * @see EventKey
 * @see TypeKey
 * @see EventSplitter
 * @see EventField
 */
// Todo: introduce type parameter
public class MultiKey
{
    protected final String key;
    protected final List<EventKey> eventKeys;

    public static <T> MultiKey valueOf(EventKey... events)
    {
        return new MultiKey(events);
    }

    public <T> MultiKey(EventKey... events)
    {
        sane(events, "events");
        this.eventKeys = Arrays.asList(events);
        this.key = Arrays.stream(events)
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
    }

    public int hashCode()
    {
        return key.hashCode();
    }

    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        if (!(other instanceof MultiKey))
        {
            return false;
        }
        MultiKey o = (MultiKey) other;
        if (o.key == null)
        {
            return (this.key == null);
        }
        return o.key.equals(this.key);
    }

    public List<EventKey> eventKeys()
    {
        return eventKeys;
    }

    public String toString()
    {
        return key;
    }
}
