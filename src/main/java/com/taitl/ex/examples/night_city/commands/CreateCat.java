package com.taitl.ex.examples.night_city.commands;

import com.taitl.ex.examples.night_city.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;

public class CreateCat
{
    public void call()
            throws ExistentialException
    {
        String tranID = Ex.begin("/api/cats/create").id();
        Ex.mutate(null, CityTestData.GREY_CAT, tranID);
        Ex.commit(tranID);
    }
}
