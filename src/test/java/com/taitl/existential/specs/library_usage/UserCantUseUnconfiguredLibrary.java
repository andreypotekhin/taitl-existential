package com.taitl.existential.specs.library_usage;

import com.taitl.existential.specs.SpecBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserCantUseUnconfiguredLibrary extends SpecBase
{
    {
        autoConfigure = false;
    }

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

    @Test
    @DisplayName("User can't send events if no rules has been configured")
    void sendingEventsToUnconfiguredLibrary()
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            String tran = ex.begin(op).id();
            ex.event(cat, tran);
        }).getMessage(), containsString("You need to configure at least one context"));
    }

    @Test
    @DisplayName("User can't send events if no rules has been configured")
    void sendingEventsToLibraryBeforeItIsConfigured()
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            ex.begin(op);
        }).getMessage(), containsString("You need to configure at least one context"));
    }
}