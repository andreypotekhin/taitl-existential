package com.taitl.ex.examples.night_city.commands;

import com.taitl.existential.transactions.Tr;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;

public class UpdateCat
{
    public void call()
            throws ExistentialException
    {
        Tr tranIDTr = Ex.begin("/api/cats/update");
        String tranID = tranIDTr.id();
        Ex.update(CityTestData.GREY_CAT, tranID);
        tranIDTr.commit();
    }
}
