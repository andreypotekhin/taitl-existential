package com.taitl.ex.logic.configuration.rules;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MatchParentNameTest
{
    @Test
    void allowsSamePath()
    {
        assertTrue(MatchParentName.matches("/api/cats", "/api/cats"));
    }

    @Test
    void allowsChildPath()
    {
        assertTrue(MatchParentName.matches("/api/cats/create", "/api/cats"));
    }

    @Test
    void rejectsParentPath()
    {
        assertFalse(MatchParentName.matches("/api/cats", "/api/cats/create"));
    }

    @Test
    void allowsWildcardChildMatchingParent()
    {
        assertTrue(MatchParentName.matches("/api/*/create", "/api/cats/create"));
    }

    @Test
    void allowsWildcardParentMatchingChild()
    {
        assertTrue(MatchParentName.matches("/api/cats/create", "/api/*"));
    }
}
