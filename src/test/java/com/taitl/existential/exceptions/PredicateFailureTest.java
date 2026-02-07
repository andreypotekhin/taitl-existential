package com.taitl.existential.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
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
    void testPredicateFailureException()
    {
        o = new PredicateFailure();
        assertEquals(null, o.getMessage());
    }

    @Test
    void testPredicateFailureExceptionString()
    {
        o = new PredicateFailure("My message");
        assertEquals("My message", o.getMessage());
    }

}
