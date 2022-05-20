package com.taitl.existential.model.cats;

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
