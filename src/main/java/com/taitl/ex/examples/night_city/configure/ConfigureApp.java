package com.taitl.ex.examples.night_city.configure;

public class ConfigureApp
{
    ConfigureEntityRules configureEntityRules = new ConfigureEntityRules();
    ConfigureTransactionRules configureTransactionRules = new ConfigureTransactionRules();
    ConfigureAccessRules configureAccessRules = new ConfigureAccessRules();

    public void configure()
    {
        configureEntityRules.configure();
    }

    public void configureWithInstances()
    {
        configureEntityRules.configureWithInstances();
    }

    public void configureMixingFluentAndBuilders()
    {
        configureEntityRules.configureMixingFluentAndBuilders();
    }

    public void configureTransactionRules()
    {
        configureTransactionRules.configureTransaction();
    }
}
