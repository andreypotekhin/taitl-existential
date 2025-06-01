package com.taitl.examples.night_city.tests;

import com.taitl.examples.night_city.commands.configure.*;
import com.taitl.existential.*;

public class CityTests
{
    public Existential ex;
    public ConfigureApp configureApp;

    public CityTests(Existential ex)
    {
        this.ex = ex;
        this.configureApp = new ConfigureApp(ex);
    }

    public void configure()
    {
        configureApp.configure();
    }

    public void configureWithInnerClasses()
    {
        configureApp.configureWithInnerClasses();
    }

    public void configureWithBuilders()
    {
        configureApp.configureWithBuilders();
    }

    public void configureMixingFluentAndBuilders()
    {
        configureApp.configureMixingFluentAndBuilders();
    }
}