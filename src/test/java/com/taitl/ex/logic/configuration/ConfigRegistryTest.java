package com.taitl.ex.logic.configuration;

import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigRegistryTest
{
    @Test
    @DisplayName("Get rejects missing config with op key message")
    void getRejectsMissingConfigWithOpKeyMessage()
    {
        ConfigRegistry registry = new ConfigRegistry(null);
        String op = "/app/orders";

        RuntimeException ex = assertThrows(RuntimeException.class, () -> registry.get(op));

        assertThat(ex.getMessage(), is("Config not found for op '" + op + "'"));
    }

    @Test
    @DisplayName("Remove rejects missing config with op key message")
    void removeRejectsMissingConfigWithOpKeyMessage()
    {
        ConfigRegistry registry = new ConfigRegistry(null);
        String op = "/app/orders";

        RuntimeException ex = assertThrows(RuntimeException.class, () -> registry.remove(op));

        assertThat(ex.getMessage(), is("Config not found for op '" + op + "'"));
    }
}
