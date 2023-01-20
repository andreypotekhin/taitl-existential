package com.taitl.existential.model.cats;

public class Building
{
    public String color;

    public Building(String color)
    {
        this.color = color;
    }

    public String color()
    {
        return color;
    }
}
