package com.taitl.examples.night_city.commands;

import com.taitl.existential.*;
import com.taitl.examples.night_city.data.*;

public class AppCommands
{
    public CreateCat createCat;

    public AppCommands()
    {
        this.createCat = new CreateCat();
    }
}
