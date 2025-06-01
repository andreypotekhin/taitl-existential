package com.taitl.examples.night_city.app;

import com.taitl.examples.night_city.commands.*;
import com.taitl.examples.night_city.commands.configure.*;
import com.taitl.existential.*;
import com.taitl.examples.night_city.commands.*;
import com.taitl.examples.night_city.commands.configure.*;

public class CityApp
{
    public Existential ex;
    public ConfigureApp configureApp;
    public AppCommands appCommands;

    public CityApp(Existential ex)
    {
        this.ex = ex;
        this.configureApp = new ConfigureApp(ex);
        this.appCommands = new AppCommands(ex);
    }

    public void configure()
    {
        configureApp.configure();
    }
}
