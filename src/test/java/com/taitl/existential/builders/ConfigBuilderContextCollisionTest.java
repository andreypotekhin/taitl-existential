package com.taitl.existential.builders;

import java.util.*;
import java.util.stream.*;
import com.taitl.existential.configs.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ConfigBuilderContextCollisionTest
{
    @Nested
    @DisplayName("ConfigBuilder.context(Context)")
    class ConfigBuilderContext
    {
        @Test
        @DisplayName("Sibling contexts stay addressable by distinct keys")
        void siblingContextsStayAddressableByDistinctKeys()
        {
            ConfigBuilder builder = new ConfigBuilder("/api/cats");
            Context create = new Context("/api/cats/create");
            Context update = new Context("/api/cats/update");

            builder.context(create).context(update);

            assertEquals(List.of("/api/cats/create", "/api/cats/update"), contextNames(builder.contexts));
        }

        @Test
        @DisplayName("Wildcard and specific contexts remain distinct")
        void wildcardAndSpecificContextsRemainDistinct()
        {
            ConfigBuilder builder = new ConfigBuilder("/api/cats/create");
            Context wildcard = new Context("/api/*/create");
            Context specific = new Context("/api/cats/create");

            builder.context(wildcard).context(specific);

            List<String> names = contextNames(builder.contexts);
            assertTrue(names.contains("/api/*/create"));
            assertTrue(names.contains("/api/cats/create"));
        }

        @Test
        @DisplayName("Parent contexts precede child contexts")
        void parentContextsPrecedeChildContexts()
        {
            ConfigBuilder builder = new ConfigBuilder("/api/cats");
            Context parent = new Context("/api/cats");
            Context child = new Context("/api/cats/create");

            builder.context(parent).context(child);

            assertEquals(List.of("/api/cats", "/api/cats/create"), contextNames(builder.contexts));
        }

        @Test
        @DisplayName("Rejects parent context under child config")
        void rejectsParentContextUnderChildConfig()
        {
            ConfigBuilder builder = new ConfigBuilder("/api/cats/create");

            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> builder.context("/api/cats"));
            assertTrue(ex.getMessage().contains("must match"));
        }

        @Test
        @DisplayName("Rejects unrelated named context")
        void rejectsUnrelatedNamedContext()
        {
            ConfigBuilder builder = new ConfigBuilder("/api/cats/create");

            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> builder.context("/admin/users"));
            assertTrue(ex.getMessage().contains("must match"));
        }

        @Test
        @DisplayName("Parameterless context uses config op key")
        void parameterlessContextUsesConfigOpKey()
        {
            ConfigBuilder builder = new ConfigBuilder("/api/cats/create");
            builder.context()
                    .invariant(String.class)
                    .create(v -> true, "ok")
                    .done()
                    .build();

            assertEquals(List.of("/api/cats/create"), contextNames(builder.contexts));
        }
    }

    private List<String> contextNames(List<Context> contexts)
    {
        return contexts.stream()
                .map(Context::name)
                .collect(Collectors.toList());
    }
}
