package com.taitl.ex.examples.night_city.model;

import com.taitl.ex.examples.night_city.model.material.*;

public class Building<T extends Material>
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
