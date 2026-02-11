package com.taitl.ex.examples.night_city.configure;

public class ConfigureApp
{
    ConfigureClassRules configureClassRules = new ConfigureClassRules();
    ConfigureAccessRules configureAccessRules = new ConfigureAccessRules();

    public void configure()
    {
        configureClassRules.configure();
    }

    public void configureWithInstances()
    {
        configureClassRules.configureWithInstances();
    }

    public void configureMixingFluentAndBuilders()
    {
        configureClassRules.configureMixingFluentAndBuilders();
    }
}