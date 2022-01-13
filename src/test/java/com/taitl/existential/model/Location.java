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

    public boolean equals(Location other)
    {
        if (other == this)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        if (other.loc == null)
        {
            return (this.loc == null);
        }
        return other.loc.equals(this.loc);
    }

    public String toString()
    {
        return "[loc=" + loc + "]";
    }
}
