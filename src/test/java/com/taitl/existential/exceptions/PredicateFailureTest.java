package com.taitl.existential.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PredicateFailureTest
{
    PredicateFailure o;

    @BeforeEach
    void setUp()
    {
    }

    @AfterEach
    void tearDown()
    {
    }

    @Test
    @DisplayName("Test predicate failure exception")
    void testPredicateFailureException()
    {
        o = new PredicateFailure();
        assertEquals(null, o.getMessage());
    }

    @Test
    @DisplayName("Test predicate failure exception string")
    void testPredicateFailureExceptionString()
    {
        o = new PredicateFailure("My message");
        assertEquals("My message", o.getMessage());
    }

}
