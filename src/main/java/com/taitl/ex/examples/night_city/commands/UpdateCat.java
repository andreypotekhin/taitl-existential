package com.taitl.ex.examples.night_city.commands;

import com.taitl.ex.examples.night_city.data.CityTestData;
import com.taitl.existential.Ex;
import com.taitl.existential.exceptions.ExistentialException;

public class UpdateCat
{
    public void call()
            throws ExistentialException
    {
        String tranID = Ex.begin("/api/cats/update").id();
        Ex.event(CityTestData.GREY_CAT, tranID);
        Ex.commit(tranID);
    }
}
