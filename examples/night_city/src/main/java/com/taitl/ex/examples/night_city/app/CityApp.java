package com.taitl.ex.examples.night_city.app;

import com.taitl.ex.examples.night_city.configure.*;

public class CityApp
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

    public void configureMixingFluentAndBuilders()
    {
        configureApp.configureMixingFluentAndBuilders();
    }

    public void configureTransactionRules()
    {
        configureApp.configureTransactionRules();
    }
}
