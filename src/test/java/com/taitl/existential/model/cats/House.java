package com.taitl.existential.model.cats;

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
