package com.taitl.ex.examples.night_city.configure;

public class ConfigureApp
{
    ConfigureClasses configureClasses = new ConfigureClasses();
    ConfigureAccess configureAccess = new ConfigureAccess();

    public void configure()
    {
        configureClasses.configure();
    }

    public void configureWithInstances()
    {
        configureClasses.configureWithInstances();
    }

    public void configureMixingFluentAndBuilders()
    {
        configureClasses.configureMixingFluentAndBuilders();
    }
}