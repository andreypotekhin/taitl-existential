package com.taitl.existential.keys;

/**
 * EventKey = EventType + TypeKey,  for retrieving EventHandlers
 * configured for a specific event type and type.
 * Examples: "Create<Doc<Json>>", "Update<Measurement<Float>>"
 */
public class EventKey
{
    protected String key;

    public static EventKey valueOf(String s)
    {
        return new EventKey(s);
    }

    public EventKey(String s)
    {
        key = s;
    }

    public EventKey(Object t)
    {
        key = t.getClass().getSimpleName();
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
        if (!(other instanceof EventKey))
        {
            return false;
        }
        EventKey o = (EventKey) other;
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
