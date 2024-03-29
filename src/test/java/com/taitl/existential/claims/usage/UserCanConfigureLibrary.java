package com.taitl.existential.claims.usage;

import com.taitl.existential.Existential;
import com.taitl.existential.contexts.Context;
import com.taitl.existential.invariants.Effect;
import com.taitl.existential.invariants.Invariant;
import com.taitl.existential.keys.TypeKey;
import com.taitl.existential.model.cats.Cat;
import com.taitl.existential.model.cats.Location;
import com.taitl.existential.model.cats.TestData;
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
    CatsTests fix;
    Cat cat;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
        op = "/api/cats";
        fix = new CatsTests(ex, op);
        cat = new Cat(TestData.BLACK_CAT.color(), TestData.BLACK_CAT.location());
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

    void configure()
    {
        fix.configure();
    }

    void configureWithBuilders()
    {
        fix.configureWithBuilders();
    }

    void configureMixingFluentAndBuilders()
    {
        fix.configureMixingFluentAndBuilders();
    }

    @Test
    @DisplayName("User must configure the library before use")
    void sendEventsToUnconfiguredLibrary() throws Exception
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            String tran = ex.transactions.begin(op);
            ex.events.send(cat, tran);
        }).getMessage(), containsString("You need to configure at least one context"));
    }

    @Test
    @DisplayName("User can configure the library")
    void configureLibrary() throws Exception
    {
        assertDoesNotThrow(() -> {
            configure();
            String tran = ex.transactions.begin(op);
            ex.events.send(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure the library using fluent style")
    void configureLibraryUsingFluentStyle() throws Exception
    {
        assertDoesNotThrow(() -> {
            configure();
            String tran = ex.transactions.begin(op);
            ex.events.send(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure the library using builders")
    void configureLibraryUsingConfigBuilder() throws Exception
    {
        assertDoesNotThrow(() -> {
            configureWithBuilders();
            String tran = ex.transactions.begin(op);
            ex.events.send(cat, tran);
        });
    }

    @Test
    @DisplayName("User can configure the library mixing fluent style and builders")
    void configureLibraryMixingFluentAndBuilders() throws Exception
    {
        assertDoesNotThrow(() -> {
            configureMixingFluentAndBuilders();
            String tran = ex.transactions.begin(op);
            ex.events.send(cat, tran);
        });
    }

    // TODO:configure with file
    // TODO:configure with env var
}