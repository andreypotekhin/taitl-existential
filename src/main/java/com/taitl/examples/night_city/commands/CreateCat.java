package com.taitl.examples.night_city.commands;

import com.taitl.examples.night_city.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;

public class CreateCat
{
    Existential ex;

    public CreateCat(Existential ex)
    {
        this.ex = ex;
    }

    public void call()
            throws ExistentialException
    {
        String tranID = ex.begin("/api/cats/create");
        ex.event(null, CityTestData.GREY_CAT, tranID);
        ex.commit(tranID);
    }
}
