package com.taitl.existential.model;

public class Cat
{
    public String color;
    public Location location;

    public Cat(String color, String location)
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
