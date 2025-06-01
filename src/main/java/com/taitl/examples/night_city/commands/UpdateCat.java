package com.taitl.examples.night_city.commands;

import com.taitl.examples.night_city.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;

public class UpdateCat
{
    Existential ex;

    public UpdateCat(Existential ex)
    {
        this.ex = ex;
    }

    public void call()
            throws ExistentialException
    {
        String tranID = ex.begin("/api/cats/update");
        ex.event(CityTestData.GREY_CAT, tranID);
        ex.commit(tranID);
    }
}
