package com.taitl.examples.night_city.model;

public class Dwelling<T extends Being>
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
