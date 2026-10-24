package com.taitl.ex.examples.night_city.commands;

import com.taitl.existential.transactions.Tr;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;

public class UpdateCat
{
    public void call()
    {
        Tr tr = Ex.begin("/api/cats/update");
        String id = tr.id();
        Ex.update(CityTestData.GREY_CAT, id);
        tr.commit();
    }
}
