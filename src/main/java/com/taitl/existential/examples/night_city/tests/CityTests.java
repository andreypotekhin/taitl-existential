package com.taitl.existential.examples.night_city.tests;

import com.taitl.existential.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.examples.night_city.commands.*;
import com.taitl.existential.examples.night_city.model.*;
import com.taitl.existential.invariants.*;

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

    public void configureWithAnonymousInnerClasses()
    {
        configureApp.configureWithAnonymousInnerClasses();
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