package com.taitl.ex.examples.night_city.commands;

import com.taitl.existential.transactions.Tr;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;

public class CreateCat
{
    public void call()
            throws ExistentialException
    {
        Tr tr = Ex.begin("/api/cats/create");
        String id = tr.id();
        Ex.transit(null, CityTestData.GREY_CAT, id);
        tr.commit();
    }
}
