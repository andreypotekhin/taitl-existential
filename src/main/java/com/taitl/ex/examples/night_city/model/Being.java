package com.taitl.ex.examples.night_city.model;

import com.taitl.ex.examples.night_city.model.taxonomy.*;

import java.util.*;

public class Being<T extends Family>
{
    public String color;
    public Location location;

    public Being(String color, String location)
    {
        this.color = color;
        this.location = new Location(location);
    }

    public String color()
    {
        return color;
    }

    public String location()
    {
        return location.toString();
    }

    public int hashCode()
    {
        return Objects.hash(color, location);
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
        Cat o = (Cat) other;
        boolean sameColor = (o.color == null && this.color == null) || o.color.equals(this.color);
        boolean sameLocation = (o.location == null && this.location == null) || o.color.equals(this.color);
        return sameColor && sameLocation;
    }
}
