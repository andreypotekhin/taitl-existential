package com.taitl.ex.examples.night_city.model;

import java.util.*;

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
        return Objects.hashCode(loc);
    }

    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (other == null || other.getClass() != getClass())
        {
            return false;
        }
        Location o = (Location) other;
        return Objects.equals(loc, o.loc);
    }

    public String toString()
    {
        return loc;
    }
}
