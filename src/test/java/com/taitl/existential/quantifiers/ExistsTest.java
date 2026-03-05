package com.taitl.existential.quantifiers;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ExistsTest
{
    @Nested
    class Constructors
    {
        @Test
        @DisplayName("Construct with collection only")
        void withCollectionOnly()
        {
            Exists<String> exists = new Exists<>(List.of("a", "boat"));
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
            assertEquals("", exists.description());
        }

        @Test
        @DisplayName("Construct with collection and description")
        void withCollectionAndDescription()
        {
            Exists<String> exists = new Exists<>(List.of("a", "boat"), "At least one value must exist");
            assertEquals("At least one value must exist", exists.description());
        }

        @Test
        @DisplayName("Construct with map only")
        void withMapOnly()
        {
            Exists<String> exists = new Exists<>(Map.of("a", 1, "boat", 2));
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
        }

        @Test
        @DisplayName("Construct map collection-predicate")
        void withMapCollectionPredicate()
        {
            Exists<String> exists = new Exists<>(Map.of("a", 1, "boat", 2), values -> values.size() == 1, 0);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
        }

        @Test
        @DisplayName("Construct map key-value bipredicate")
        void withMapKeyValueBipredicate()
        {
            Exists<String> exists = new Exists<>(Map.of("a", 1, "boat", 2), (key, value) -> key.length() > value);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("a"));
        }
    }
}
