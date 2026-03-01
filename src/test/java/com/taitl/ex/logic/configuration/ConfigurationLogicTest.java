package com.taitl.ex.logic.configuration;

import com.taitl.existential.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigurationLogicTest
{
    @Test
    @DisplayName("Config rejects missing config with op key message")
    void configRejectsMissingConfigWithOpKeyMessage()
    {
        try (Existential ex = new Existential())
        {
            String op = "/app/orders";

            RuntimeException error = assertThrows(RuntimeException.class, () -> ex.configs().config(op));

            assertThat(error.getMessage(), is("Config not found for op '" + op + "'"));
        }
    }
}
