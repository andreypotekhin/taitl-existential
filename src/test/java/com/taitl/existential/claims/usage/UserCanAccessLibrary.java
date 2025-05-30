package com.taitl.existential.claims.usage;

import com.taitl.examples.night_city.data.*;
import com.taitl.examples.night_city.model.*;
import com.taitl.examples.night_city.tests.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class UserCanAccessLibrary
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
        fixt = new CityTests(ex);
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

    @Test
    @DisplayName("User can access library")
    void accessLibrary() throws Exception
    {
        configure();
        String tran = ex.begin(op);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can change library options programmatically ")
    void changeLibraryOptions() throws Exception
    {
        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(false));
        ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(true));
        ex.toggle(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(false));
    }

    @Test
    @DisplayName("User can send an entity event to library")
    void sendEntityEvent() throws Exception
    {
        configure();
        String tran = ex.begin(op);
        ex.event(null, cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can send an entity event using a type key")
    void sendEntityEventWithTypeKey() throws Exception
    {
        configure();
        String tran = ex.begin(op);
        ex.event(null, cat, new TypeKey<Cat>(Cat.class), tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can record access to entity")
    void recordEntityAccess() throws Exception
    {
        configure();
        String tran = ex.begin(op);
        ex.read(cat, tran);
        ex.write(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can record access to entity using type key")
    void recordEntityAccessWithTypeKey() throws Exception
    {
        configure();
        String tran = ex.begin(op);
        ex.read(cat, new TypeKey<Cat>(Cat.class), tran);
        ex.write(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can begin transaction")
    void begin() throws Exception
    {
        configure();
        String tran = ex.begin(op);
    }

    @Test
    @DisplayName("User can commit transaction")
    void commit() throws Exception
    {
        configure();
        String tran = ex.begin(op);
        ex.event(cat, tran);
        ex.commit(tran);
    }

    @Test
    @DisplayName("User can rollback transaction")
    void rollback() throws Exception
    {
        configure();
        String tran = ex.begin(op);
        ex.event(cat, tran);
        ex.rollback(tran);
    }

    @Test
    @DisplayName("User can use checkpoint to pre-commit transaction")
    void checkpoint() throws Exception
    {
        configure();
        String tran = ex.begin(op);
        ex.event(cat, tran);
        ex.check(tran);
    }

    @Test
    @DisplayName("User can't sent events to library which hasn't been configured")
    void sendEventsToUnconfiguredLibrary() throws Exception
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            String tran = ex.begin(op);
            ex.event(cat, tran);
        }).getMessage(), containsString("You need to configure at least one context"));
    }

    @Test
    @DisplayName("User can't sent events to library before it is configured")
    void sendEventsToLibraryBeforeItIsConfigured() throws Exception
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            ex.begin(op);
        }).getMessage(), containsString("You need to configure at least one context"));
    }
}