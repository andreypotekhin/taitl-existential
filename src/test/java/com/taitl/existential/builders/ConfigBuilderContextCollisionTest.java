package com.taitl.existential.builders;

import java.util.*;
import java.util.stream.*;
import com.taitl.existential.Existential;
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
            ConfigBuilder builder = new ConfigBuilder(new Existential());
            Context create = new Context("/api/cats/create");
            Context update = new Context("/api/cats/update");

            builder.context(create).context(update);

            assertEquals(List.of("/api/cats/create", "/api/cats/update"), contextNames(builder.contexts));
        }

        @Test
        @DisplayName("Wildcard and specific contexts remain distinct")
        void wildcardAndSpecificContextsRemainDistinct()
        {
            ConfigBuilder builder = new ConfigBuilder(new Existential());
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
            ConfigBuilder builder = new ConfigBuilder(new Existential());
            Context parent = new Context("/api/cats");
            Context child = new Context("/api/cats/create");

            builder.context(parent).context(child);

            assertEquals(List.of("/api/cats", "/api/cats/create"), contextNames(builder.contexts));
        }

        @Test
        @DisplayName("Accepts parent and child named contexts on root builder")
        void acceptsParentAndChildNamedContextsOnRootBuilder()
        {
            ConfigBuilder builder = new ConfigBuilder(new Existential());

            assertDoesNotThrow(() -> {
                builder.context("/api/cats/create");
                builder.context("/api/cats");
            });
        }

        @Test
        @DisplayName("Accepts unrelated named contexts")
        void acceptsUnrelatedNamedContext()
        {
            ConfigBuilder builder = new ConfigBuilder(new Existential());

            assertDoesNotThrow(() -> builder.context("/admin/users"));
        }

        @Test
        @DisplayName("Rejects blank context name")
        void rejectsBlankContextName()
        {
            ConfigBuilder builder = new ConfigBuilder(new Existential());
            assertThrows(IllegalArgumentException.class, () -> builder.context(" "));
        }
    }

    private List<String> contextNames(List<Context> contexts)
    {
        return contexts.stream()
                .map(Context::name)
                .collect(Collectors.toList());
    }
}
