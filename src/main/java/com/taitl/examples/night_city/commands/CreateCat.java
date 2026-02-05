package com.taitl.examples.night_city.commands;

import com.taitl.examples.night_city.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;

public class CreateCat
{
    public void call()
            throws ExistentialException
    {
        String tranID = Ex.begin("/api/cats/create");
        Ex.event(null, CityTestData.GREY_CAT, tranID);
        Ex.commit(tranID);
    }
}
