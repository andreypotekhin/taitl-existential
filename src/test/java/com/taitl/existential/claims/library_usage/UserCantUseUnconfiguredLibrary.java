package com.taitl.existential.claims.library_usage;

import com.taitl.existential.claims.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCantUseUnconfiguredLibrary extends ClaimBase
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
    @DisplayName("User can't sent events to library which hasn't been configured")
    void sendingEventsToUnconfiguredLibrary()
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            String tran = ex.begin(op);
            ex.event(cat, tran);
        }).getMessage(), containsString("You need to configure at least one context"));
    }

    @Test
    @DisplayName("User can't sent events to library before it has been configured")
    void sendingEventsToLibraryBeforeItIsConfigured()
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            ex.begin(op);
        }).getMessage(), containsString("You need to configure at least one context"));
    }
}