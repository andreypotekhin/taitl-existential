package com.taitl.examples.night_city.commands;

import com.taitl.existential.*;
import com.taitl.examples.night_city.data.*;

public class AppCommands
{
    Existential ex;
    public CreateCat createCat;

    public AppCommands(Existential ex)
    {
        this.ex = ex;
        this.createCat = new CreateCat(ex);
    }
}
