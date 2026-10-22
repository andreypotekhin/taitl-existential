package com.taitl.ex.examples.night_city.app;

import com.taitl.ex.examples.night_city.commands.*;
import com.taitl.ex.examples.night_city.configure.*;

public class CityApplication
{
    public ConfigureApp configureApp;

    public AppCommands appCommands;

    public CityApplication()
    {
        this.configureApp = new ConfigureApp();
        this.appCommands = new AppCommands();
    }

    public void configure()
    {
        configureApp.configure();
    }
}
