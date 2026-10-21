package com.taitl.ex.examples.night_city.commands;

import com.taitl.ex.common.annotations.*;

public class AppCommands
{
    @Logic
    public CreateCat createCat;

    public AppCommands()
    {
        this.createCat = new CreateCat();
    }
}
