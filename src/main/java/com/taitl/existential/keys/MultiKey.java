package com.taitl.existential.keys;

import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;

import java.util.*;

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
public class MultiKey<T>
{
    protected final String key;
    protected final List<EventKey<T>> eventKeys;

    @SafeVarargs
    public static <T> MultiKey<T> valueOf(EventKey<? extends T>... events)
    {
        return new MultiKey<>(events);
    }

    public static <T> MultiKey<T> valueOf(List<EventKey<T>> events)
    {
        return new MultiKey<>(events);
    }

    @SafeVarargs
    public MultiKey(EventKey<? extends T>... events)
    {
        sane(events, "events");
        this.eventKeys = castEventKeys(events);
        this.key = joinedKey(this.eventKeys);
    }

    public MultiKey(List<EventKey<T>> events)
    {
        sane(events, "events");
        this.eventKeys = new ArrayList<>(events);
        this.key = joinedKey(this.eventKeys);
    }

    protected static <T> String joinedKey(List<EventKey<T>> eventKeys)
    {
        if (eventKeys.isEmpty())
        {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < eventKeys.size(); i++)
        {
            if (i > 0)
            {
                builder.append(',');
            }
            builder.append(eventKeys.get(i));
        }
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    protected static <T> List<EventKey<T>> castEventKeys(EventKey<? extends T>[] eventKeys)
    {
        List<EventKey<T>> typed = new ArrayList<>(eventKeys.length);
        for (EventKey<? extends T> eventKey : eventKeys)
        {
            typed.add((EventKey<T>) eventKey);
        }
        return typed;
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
        if (!(other instanceof MultiKey<?>))
        {
            return false;
        }
        MultiKey<?> o = (MultiKey<?>) other;
        if (o.key == null)
        {
            return (this.key == null);
        }
        return o.key.equals(this.key);
    }

    public List<EventKey<T>> eventKeys()
    {
        return eventKeys;
    }

    public String toString()
    {
        return key;
    }
}
