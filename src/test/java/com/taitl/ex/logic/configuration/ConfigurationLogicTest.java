package com.taitl.ex.logic.configuration;

import com.taitl.existential.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

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

    @Test
    @DisplayName("Config indexes wildcard context for concrete operation")
    void configIndexesWildcardContextForConcreteOperation() throws Exception
    {
        try (Existential ex = new Existential())
        {
            AtomicInteger updateCalls = new AtomicInteger();

            // @formatter:off
            ex.configure()
                    .context("/api/*/update")
                        .effect(String.class)
                        .update(v -> updateCalls.incrementAndGet(), "wildcard update")
                        .done();
            // @formatter:on

            String tran = ex.begin("/api/cats/update").id();
            ex.update("ok", tran);
            ex.commit(tran);

            assertEquals(1, updateCalls.get());
        }
    }

    @Test
    @DisplayName("Begin finalizes configuration when build was never called")
    void beginFinalizesWithoutExplicitBuild()
    {
        try (Existential ex = new Existential())
        {
            ex.configure()
                    .context("/api/cats/update")
                    .effect(String.class)
                    .update(v -> {
                    }, "update")
                    .done();

            assertDoesNotThrow(() -> {
                String tran = ex.begin("/api/cats/update").id();
                ex.update("ok", tran);
                ex.commit(tran);
            });
        }
    }
}
