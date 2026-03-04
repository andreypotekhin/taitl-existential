package com.taitl.ex.logic.events;

import com.taitl.ex.cross.caching.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

class TypeKeyCacheTest
{
    TypeKeyCache cache;

    @BeforeEach
    void setup()
    {
        cache = new TypeKeyCache();
    }

    @Nested
    class Caching
    {
        @Test
        @DisplayName("Caches short names per class")
        void shortNames()
        {
            TypeKey<String> a = cache.get("one", false);
            TypeKey<String> b = cache.get("two", false);

            assertThat(a == b, is(true));
            assertThat(a.toString(), is("String"));
        }

        @Test
        @DisplayName("Caches full names separately from short names")
        void fullNamesSeparately()
        {
            TypeKey<String> shortName = cache.get(String.class, false);
            TypeKey<String> fullName = cache.get(String.class, true);

            assertThat(shortName == fullName, is(false));
            assertThat(shortName.toString(), is("String"));
            assertThat(fullName.toString(), is("java.lang.String"));
        }
    }
}
