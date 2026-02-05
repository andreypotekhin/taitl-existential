package com.taitl.examples.night_city.app;

import com.taitl.examples.night_city.commands.*;
import com.taitl.examples.night_city.configure.*;
import com.taitl.existential.*;

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
