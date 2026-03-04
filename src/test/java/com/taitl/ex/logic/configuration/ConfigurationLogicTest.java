package com.taitl.ex.logic.configuration;

import com.taitl.existential.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigurationLogicTest
{
    private Existential ex;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

    @Nested
    class ConfigLookup
    {
        @Test
        @DisplayName("Rejects missing config with op key message")
        void rejectsMissingConfig()
        {
            RuntimeException error = assertThrows(RuntimeException.class, () -> ex.configs().config("/app/orders"));
            assertThat(error.getMessage(), is("Config not found for op '/app/orders'"));
        }
    }

    @Nested
    class WildcardContexts
    {
        @Test
        @DisplayName("Indexes wildcard context for concrete operation")
        void indexesConcreteOperation() throws Exception
        {
            AtomicInteger updateCalls = new AtomicInteger();

            // @formatter:off
            ex.configure()
                    .context("/api/*/update")
                        .effect(String.class)
                        .update(v -> updateCalls.incrementAndGet(), "wildcard update");
            // @formatter:on

            String tran = ex.begin("/api/cats/update").id();
            ex.update("ok", tran);
            ex.commit(tran);

            assertEquals(1, updateCalls.get());
        }
    }

    @Nested
    class BuildLifecycle
    {
        @Test
        @DisplayName("Begin finalizes configuration when build was never called")
        void finalizesOnBegin()
        {
            ex.configure()
                    .context("/api/cats/update")
                    .effect(String.class)
                    .update(v -> {
                    }, "update");

            assertDoesNotThrow(() -> {
                String tran = ex.begin("/api/cats/update").id();
                ex.update("ok", tran);
                ex.commit(tran);
            });
        }
    }
}
