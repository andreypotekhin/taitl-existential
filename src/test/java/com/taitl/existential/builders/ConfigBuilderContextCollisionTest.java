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
            ConfigBuilder builder = new ConfigBuilder("/api/cats/create");
            Context cats = new Context("/api/cats");
            Context dogs = new Context("/api/dogs");

            builder.context(cats).context(dogs);

            assertEquals(List.of("/api/cats", "/api/dogs"), contextNames(builder.contexts));
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
            ConfigBuilder builder = new ConfigBuilder("/api/cats/create");
            Context parent = new Context("/api/cats");
            Context child = new Context("/api/cats/create");

            builder.context(parent).context(child);

            assertEquals(List.of("/api/cats", "/api/cats/create"), contextNames(builder.contexts));
        }
    }

    private List<String> contextNames(List<Context> contexts)
    {
        return contexts.stream()
                .map(Context::name)
                .collect(Collectors.toList());
    }
}
