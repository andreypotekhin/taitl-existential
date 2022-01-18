package com.taitl.existential;

public class EventKey
{
    protected String eventid;

    public EventKey(Object t)
    {
        eventid = t.getClass().getSimpleName();
    }

    public <T> EventKey()
    {
        eventid = ((T) null).getClass().getSimpleName();
    }

    public static EventKey valueOf(String s)
    {
        return new EventKey(s);
    }

    public int hashCode()
    {
        return eventid.hashCode();
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
        EventKey o = (EventKey)other; 
        if (o.eventid == null)
        {
            return (this.eventid == null);
        }
        return o.eventid.equals(this.eventid);
    }

    public String toString()
    {
        return eventid;
    }
}
