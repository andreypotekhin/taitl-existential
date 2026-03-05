package com.taitl.ex.examples.night_city.configure;

public class ConfigureApp
{
    ConfigureEntities configureEntities = new ConfigureEntities();
    ConfigureTransaction configureTransaction = new ConfigureTransaction();
    ConfigureIntents configureIntents = new ConfigureIntents();

    public void configure()
    {
        configureEntities.configure();
    }

    public void configureWithInstances()
    {
        configureEntities.configureWithInstances();
    }

    public void configureMixingFluentAndBuilders()
    {
        configureEntities.configureMixingFluentAndBuilders();
    }

    public void configureTransactionRules()
    {
        configureTransaction.configureTransaction();
    }
}
