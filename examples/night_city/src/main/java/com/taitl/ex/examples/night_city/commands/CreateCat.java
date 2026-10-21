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
        Tr tranIDTr = Ex.begin("/api/cats/create");
        String tranID = tranIDTr.id();
        Ex.transit(null, CityTestData.GREY_CAT, tranID);
        tranIDTr.commit();
    }
}
