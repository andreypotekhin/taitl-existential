package com.taitl.existential.model;

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
