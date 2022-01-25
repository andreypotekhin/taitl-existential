package com.taitl.existential.model;

public class Location
{
    public String loc;

    public Location(String location)
    {
        this.loc = location;
    }

    public String location()
    {
        return loc;
    }

    public int hashCode()
    {
        return loc.hashCode();
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
        Location o = (Location) other;
        if (o.loc == null)
        {
            return (this.loc == null);
        }
        return o.loc.equals(this.loc);
    }

    public String toString()
    {
        return loc;
    }
}
