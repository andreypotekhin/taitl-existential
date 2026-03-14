package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.common.annotations.*;

public class ConfigureApp
{
    @Logic
    ConfigureEntities configureEntities = new ConfigureEntities();

    @Logic
    ConfigureTransaction configureTransaction = new ConfigureTransaction();

    @Logic
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
