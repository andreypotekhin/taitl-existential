package com.taitl.ex.examples.night_city.app;

import com.taitl.ex.examples.night_city.commands.*;
import com.taitl.ex.examples.night_city.configure.*;

public class CityApp
{
    public ConfigureApp configureApp;
    public AppCommands appCommands;

    public CityApp()
    {
        this.configureApp = new ConfigureApp();
        this.appCommands = new AppCommands();
    }

    public void configure()
    {
        configureApp.configure();
    }
}
