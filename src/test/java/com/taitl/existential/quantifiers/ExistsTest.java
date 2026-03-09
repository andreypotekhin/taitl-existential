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
        @DisplayName("Construct with set only")
        void withSetOnly()
        {
            Exists<String> exists = new Exists<>(Set.of("a", "boat"));
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
            assertEquals("", exists.description());
        }

        @Test
        @DisplayName("Construct with set and description")
        void withSetAndDescription()
        {
            Exists<String> exists = new Exists<>(Set.of("a", "boat"), "At least one value must exist");
            assertEquals("At least one value must exist", exists.description());
        }

        @Test
        @DisplayName("Construct with set entity-value bipredicate")
        void withSetEntityValueBipredicate()
        {
            Exists<String> exists = new Exists<>(Set.of(new String("boat")),
                    (entity, matched) -> entity != matched && entity.equals(matched));

            assertTrue(exists.test(new String("boat")));
            assertFalse(exists.test("goat"));
        }

        @Test
        @DisplayName("Construct with collection preserves duplicates in matching values")
        void withCollectionPreservesDuplicates()
        {
            Exists<String> exists = new Exists<>(List.of("boat", "boat"), values -> values.size() == 2, 0);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
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
        @DisplayName("Construct set entity-matches bipredicate")
        void withSetEntityMatchesBipredicate()
        {
            Exists<String> exists = new Exists<>(Set.of("a", "boat"),
                    (String entity, Set<String> matches) -> matches.size() == 1 && matches.contains(entity), "set",
                    "");
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

        @Test
        @DisplayName("Construct map entity-matches bipredicate")
        void withMapEntityMatchesBipredicate()
        {
            Exists<String> exists = new Exists<>(Map.of("a", 1, "boat", 2),
                    (entity, matches) -> matches.size() == 1 && matches.contains(entity), 0);
            assertTrue(exists.test("boat"));
            assertFalse(exists.test("goat"));
        }
    }
}
