package com.taitl.existential.claims.library_configuration;

import com.taitl.existential.claims.*;
import com.taitl.existential.constants.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureLibrary extends ClaimBase
{
    @BeforeEach
    public void setup()
    {
        super.setup();
    }

    @AfterEach
    public void cleanup()
    {
        super.cleanup();
    }

    void configureWithInstances()
    {
        fixt.configureWithInstances();
    }

    void configureMixingFluentAndBuilders()
    {
        fixt.configureMixingFluentAndBuilders();
    }

    @Test
    @DisplayName("User can change library configuration options programmatically ")
    void changeLibraryOptions()
    {
        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(false));
        ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(true));
        ex.toggle(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(false));
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

    @Test
    @DisplayName("User can configure the library using builders")
    void configureLibraryUsingConfigBuilder()
    {
        assertDoesNotThrow(() -> {
            configure();
            String tran = ex.begin(op);
            ex.event(cat, tran);
        });
    }

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