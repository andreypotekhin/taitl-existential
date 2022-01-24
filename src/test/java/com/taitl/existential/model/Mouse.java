package com.taitl.existential.model;

public class Mouse
{
    public String color;
    public Location location;

    public Mouse(String color, String location)
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
