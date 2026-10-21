package com.taitl.ex.examples.night_city.model;

import com.taitl.ex.examples.night_city.model.material.*;

public class House extends Building<Brick>
{
    public Address address;
    public boolean hasRoof = true;

    public House(String color, Address address)
    {
        super(color);
        this.address = address;
    }

    public boolean hasRoof()
    {
        return hasRoof;
    }
}
