package com.taitl.ex.examples.night_city.commands;

import com.taitl.ex.examples.night_city.data.CityTestData;
import com.taitl.existential.Ex;
import com.taitl.existential.exceptions.ExistentialException;

public class CreateCat
{
    public void call()
            throws ExistentialException
    {
        String tranID = Ex.begin("/api/cats/create").id();
        Ex.event(null, CityTestData.GREY_CAT, tranID);
        Ex.commit(tranID);
    }
}
