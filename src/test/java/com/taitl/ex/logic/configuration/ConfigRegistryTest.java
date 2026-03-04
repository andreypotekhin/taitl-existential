package com.taitl.ex.logic.configuration;

import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigRegistryTest
{
    ConfigRegistry registry;

    @BeforeEach
    void setup()
    {
        registry = new ConfigRegistry(null);
    }

    @Nested
    class MissingConfig
    {
        @Test
        @DisplayName("Get rejects missing config with op key message")
        void getRejects()
        {
            RuntimeException ex = assertThrows(RuntimeException.class, () -> registry.get("/app/orders"));
            assertThat(ex.getMessage(), is("Config not found for op '/app/orders'"));
        }

        @Test
        @DisplayName("Remove rejects missing config with op key message")
        void removeRejects()
        {
            RuntimeException ex = assertThrows(RuntimeException.class, () -> registry.remove("/app/orders"));
            assertThat(ex.getMessage(), is("Config not found for op '/app/orders'"));
        }
    }
}
