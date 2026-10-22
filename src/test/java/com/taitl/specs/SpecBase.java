package com.taitl.specs;

import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.app.*;
import com.taitl.existential.*;

public class SpecBase
{
    protected Existential ex;
    protected Existential prev;
    protected String op;
    protected CityApp fixt;
    protected Cat cat;
    protected boolean autoConfigure = true;

    protected void setup()
    {
        ex = new Existential();
        prev = Ex.instance(ex);
        op = "/api/cats";
        fixt = new CityApp();
        cat = new Cat(CityTestData.BLACK_CAT.color(), CityTestData.BLACK_CAT.location());
        if (autoConfigure)
        {
            configure();
        }
    }

    protected void cleanup()
    {
        Ex.instance(prev);
        ex.close();
    }

    protected void configure()
    {
        fixt.configure();
    }
}
