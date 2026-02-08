package com.taitl.existential.claims.usage;

import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.tests.*;
import com.taitl.existential.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureLibrary
{
    Existential ex;
    Existential prev;
    String op;
    CityTests fixt;
    Cat cat;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
        prev = Ex.instance(ex);
        op = "/api/cats";
        fixt = new CityTests();
        cat = new Cat(CityTestData.BLACK_CAT.color(), CityTestData.BLACK_CAT.location());
    }

    @AfterEach
    void cleanup()
    {
        Ex.instance(prev);
        ex.close();
    }

    void configure()
    {
        fixt.configure();
    }

    // void configureWithClasses()
    // {
    // fixt.configureWithClasses();
    // }

    void configureWithInstances()
    {
        fixt.configureWithInstances();
    }

    void configureMixingFluentAndBuilders()
    {
        fixt.configureMixingFluentAndBuilders();
    }

    @Test
    @DisplayName("User must configure the library before use")
    void sendEventsToUnconfiguredLibrary()
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            String tran = ex.begin(op);
            ex.event(cat, tran);
        }).getMessage(), containsString("You need to configure at least one context"));
    }

    @Test
    @DisplayName("User can configure the library using builders")
    void configureLibrary()
    {
        assertDoesNotThrow(() -> {
            configure();
            String tran = ex.begin(op);
            ex.event(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure the library using fluent style")
    void configureLibraryUsingFluentStyle()
    {
        assertDoesNotThrow(() -> {
            configureWithInstances();
            String tran = ex.begin(op);
            ex.event(cat, tran);
        });
    }

    // @Test
    // @DisplayName("User can configure the library using builders")
    // void configureLibraryUsingConfigBuilder()
    // {
    // assertDoesNotThrow(() -> {
    // configureWithClasses();
    // String tran = ex.begin(op);
    // ex.event(cat, tran);
    // });
    // }

    @Test
    @DisplayName("User can configure the library mixing fluent style and builders")
    void configureLibraryMixingFluentAndBuilders()
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