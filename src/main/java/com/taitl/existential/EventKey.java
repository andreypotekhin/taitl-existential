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

    public int hashCode()
    {
        return eventid.hashCode();
    }

    public boolean equals(EventKey other)
    {
        if (other == this)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        if (other.eventid == null)
        {
            return (this.eventid == null);
        }
        return other.eventid.equals(this.eventid);
    }

    public String toString()
    {
        return eventid;
    }
}
