package com.taitl.existential.examples.night_city.model;

public class House extends Building
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
