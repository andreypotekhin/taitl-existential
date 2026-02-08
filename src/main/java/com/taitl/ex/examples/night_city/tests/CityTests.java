package com.taitl.ex.examples.night_city.tests;

import com.taitl.ex.examples.night_city.configure.*;

public class CityTests
{
    public ConfigureApp configureApp = new ConfigureApp();

    public void configure()
    {
        configureApp.configure();
    }

    public void configureWithInstances()
    {
        configureApp.configureWithInstances();
    }

    // public void configureWithClasses()
    // {
    // configureApp.configureWithClasses();
    // }

    public void configureMixingFluentAndBuilders()
    {
        configureApp.configureMixingFluentAndBuilders();
    }
}