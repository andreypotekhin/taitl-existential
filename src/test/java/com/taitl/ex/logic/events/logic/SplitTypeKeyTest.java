package com.taitl.ex.logic.events.logic;

import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

class SplitTypeKeyTest
{
    private SplitTypeKey splitter;

    @BeforeEach
    void setup()
    {
        splitter = new SplitTypeKey();
    }

    @Nested
    class Root
    {
        @Test
        @DisplayName("Removes generic part and keeps only type name")
        void removesGenericPart()
        {
            assertEquals("Read", splitter.root("Read<Cat<JSON>>"));
        }

        @Test
        @DisplayName("Supports plain names and whitespace")
        void supportsWhitespace()
        {
            assertEquals("Write", splitter.root("  Write  "));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Split
    {
        private Set<String> expectedMulti;

        @BeforeAll
        void setupExpected()
        {
            expectedMulti = Set.of(
                    "T<A<X>,B<Y>>",
                    "T<A<X>,B<?>>",
                    "T<A<X>,B>",
                    "T<A<?>,B<Y>>",
                    "T<A<?>,B<?>>",
                    "T<A<?>,B>",
                    "T<A,B<Y>>",
                    "T<A,B<?>>",
                    "T<A,B>");
        }

        @Test
        @DisplayName("Single dimension")
        void single()
        {
            Set<String> keys = splitter.split(TypeKey.valueOf("T<A<X>>"))
                    .stream()
                    .map(TypeKey::toString)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            assertEquals(Set.of("T<A<X>>", "T<A<?>>", "T<A>"), keys);
        }

        @Test
        @DisplayName("Multiple dimensions")
        void multiple()
        {
            Set<String> keys = splitter.split(TypeKey.valueOf("T<A<X>,B<Y>>"))
                    .stream()
                    .map(TypeKey::toString)
                    .collect(Collectors.toSet());

            assertEquals(expectedMulti, keys);
        }
    }
}
