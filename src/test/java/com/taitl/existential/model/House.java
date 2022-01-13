package com.taitl.existential.model;

public class House
{
    public Address address;
    public boolean hasRoof = true;

    public House(Address address)
    {
        this.address = address;
    }

    public boolean hasRoof()
    {
        return hasRoof;
    }
}
