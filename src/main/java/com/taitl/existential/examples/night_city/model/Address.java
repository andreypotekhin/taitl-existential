package com.taitl.existential.examples.night_city.model;

public class Address extends Location
{
    public Address(String addr)
    {
        super(addr);
    }

    public String address()
    {
        return loc;
    }
}
