package com.taitl.examples.night_city.tests;

import com.taitl.examples.night_city.configure.*;
import com.taitl.existential.*;

public class CityTests
{
    public ConfigureApp configureApp;

    public CityTests()
    {
        this.configureApp = new ConfigureApp();
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