package com.taitl.existential.keys;

import com.taitl.ex.logic.configuration.indexes.data.EventField;
import com.taitl.ex.logic.events.logic.EventSplitter;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.taitl.ex.common.helper.Args.sane;

/**
 * Combines several event keys {@link EventKey} into a single key.
 * The event keys usually relate to a single event that has been split
 * into elementary events by EventSplitter.
 * MultiKey for accessing the EventField.
 *
 * Example:
 * Event keys: Create<Doc<JSON>>, Create<Doc<?>>, Create<Doc>
 * MultuKey:  "Create<Doc<JSON>>,Create<Doc<?>>,Create<Doc>"
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

    public static <T> MultiKey valueOf(EventKey... events)
    {
        return new MultiKey(events);
    }

    public <T> MultiKey(EventKey... events)
    {
        sane(events, "events");
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

    public String toString()
    {
        return key;
    }
}
