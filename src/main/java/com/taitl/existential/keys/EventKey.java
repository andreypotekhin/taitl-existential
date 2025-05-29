package com.taitl.existential.keys;

/**
 * Event + TypeKey, for use as key in EventHandler.
 * Examples: "Create<Doc<Json>>", "Update<Measurement<Float>>"
 */
public class EventKey
{
    protected String eventkey;

    public static EventKey valueOf(String s)
    {
        return new EventKey(s);
    }

    public EventKey(String s)
    {
        eventkey = s;
    }

    public EventKey(Object t)
    {
        eventkey = t.getClass().getSimpleName();
    }

    public int hashCode()
    {
        return eventkey.hashCode();
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
        if (o.eventkey == null)
        {
            return (this.eventkey == null);
        }
        return o.eventkey.equals(this.eventkey);
    }

    public String toString()
    {
        return eventkey;
    }
}
