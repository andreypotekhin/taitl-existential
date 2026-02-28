package com.taitl.existential.specs.library_usage;

import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

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
            ex.change(cat, tran);
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