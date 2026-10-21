package com.taitl.ex.examples.night_city.model;

import com.taitl.ex.examples.night_city.model.material.*;

public class Dwelling<T extends Being<?>, M extends Material>
{
    public String color;
    public Location location;

    public Dwelling(String color, String location)
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
}
