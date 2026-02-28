package com.taitl.ex.examples.night_city.commands;

import com.taitl.ex.examples.night_city.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;

public class UpdateCat
{
    public void call()
            throws ExistentialException
    {
        String tranID = Ex.begin("/api/cats/update").id();
        Ex.change(CityTestData.GREY_CAT, tranID);
        Ex.commit(tranID);
    }
}
