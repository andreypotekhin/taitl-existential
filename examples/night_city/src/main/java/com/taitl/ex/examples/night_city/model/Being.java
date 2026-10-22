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
        if (other == null || other.getClass() != getClass())
        {
            return false;
        }
        Being<?> o = (Being<?>) other;
        return Objects.equals(color, o.color) && Objects.equals(location, o.location);
    }
}
