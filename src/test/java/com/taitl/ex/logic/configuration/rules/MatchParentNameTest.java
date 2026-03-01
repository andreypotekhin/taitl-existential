package com.taitl.ex.logic.configuration.rules;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MatchParentNameTest
{
    @Test
    @DisplayName("Allows same path")
    void allowsSamePath()
    {
        assertTrue(MatchParentName.matches("/api/cats", "/api/cats"));
    }

    @Test
    @DisplayName("Allows child path")
    void allowsChildPath()
    {
        assertTrue(MatchParentName.matches("/api/cats/create", "/api/cats"));
    }

    @Test
    @DisplayName("Rejects parent path")
    void rejectsParentPath()
    {
        assertFalse(MatchParentName.matches("/api/cats", "/api/cats/create"));
    }

    @Test
    @DisplayName("Allows wildcard child matching parent")
    void allowsWildcardChildMatchingParent()
    {
        assertTrue(MatchParentName.matches("/api/*/create", "/api/cats/create"));
    }

    @Test
    @DisplayName("Allows wildcard parent matching child")
    void allowsWildcardParentMatchingChild()
    {
        assertTrue(MatchParentName.matches("/api/cats/create", "/api/*"));
    }
}
