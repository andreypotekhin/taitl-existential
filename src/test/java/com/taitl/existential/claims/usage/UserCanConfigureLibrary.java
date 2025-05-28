package com.taitl.existential.claims.usage;

import com.taitl.existential.Existential;
import com.taitl.existential.examples.night_city.model.Cat;
import com.taitl.existential.examples.night_city.data.CityTestData;
import com.taitl.existential.examples.night_city.tests.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserCanConfigureLibrary
{
    Existential ex;
    String op;
    CityTests fixt;
    Cat cat;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
        op = "/api/cats";
        fixt = new CityTests(ex, op);
        cat = new Cat(CityTestData.BLACK_CAT.color(), CityTestData.BLACK_CAT.location());
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

    void configure()
    {
        fixt.configure();
    }

    void configureWithBuilders()
    {
        fixt.configureWithBuilders();
    }

    void configureMixingFluentAndBuilders()
    {
        fixt.configureMixingFluentAndBuilders();
    }

    @Test
    @DisplayName("User must configure the library before use")
    void sendEventsToUnconfiguredLibrary() throws Exception
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            String tran = ex.begin(op);
            ex.event(cat, tran);
        }).getMessage(), containsString("You need to configure at least one context"));
    }

    @Test
    @DisplayName("User can configure the library")
    void configureLibrary() throws Exception
    {
        assertDoesNotThrow(() -> {
            configure();
            String tran = ex.begin(op);
            ex.event(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure the library using fluent style")
    void configureLibraryUsingFluentStyle() throws Exception
    {
        assertDoesNotThrow(() -> {
            configure();
            String tran = ex.begin(op);
            ex.event(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure the library using builders")
    void configureLibraryUsingConfigBuilder() throws Exception
    {
        assertDoesNotThrow(() -> {
            configureWithBuilders();
            String tran = ex.begin(op);
            ex.event(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure the library mixing fluent style and builders")
    void configureLibraryMixingFluentAndBuilders() throws Exception
    {
        assertDoesNotThrow(() -> {
            configureMixingFluentAndBuilders();
            String tran = ex.begin(op);
            ex.event(cat, tran);
        });
    }

    // TODO:configure with file
    // TODO:configure with env var
}