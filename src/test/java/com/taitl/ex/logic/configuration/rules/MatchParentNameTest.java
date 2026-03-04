package com.taitl.ex.logic.configuration.rules;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MatchParentNameTest
{
    @Nested
    class ExactAndHierarchy
    {
        @Test
        @DisplayName("Allows same path")
        void same()
        {
            assertTrue(MatchParentName.matches("/api/cats", "/api/cats"));
        }

        @Test
        @DisplayName("Allows child path")
        void child()
        {
            assertTrue(MatchParentName.matches("/api/cats/create", "/api/cats"));
        }

        @Test
        @DisplayName("Rejects parent path")
        void parent()
        {
            assertFalse(MatchParentName.matches("/api/cats", "/api/cats/create"));
        }
    }

    @Nested
    class Wildcards
    {
        @Test
        @DisplayName("Allows wildcard child matching parent")
        void childMatchingParent()
        {
            assertTrue(MatchParentName.matches("/api/*/create", "/api/cats/create"));
        }

        @Test
        @DisplayName("Allows wildcard parent matching child")
        void parentMatchingChild()
        {
            assertTrue(MatchParentName.matches("/api/cats/create", "/api/*"));
        }
    }
}
