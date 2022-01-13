package com.taitl.existential.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PredicateFailureExceptionTest
{
    PredicateFailureException o;

    @BeforeEach
    void setUp() throws Exception
    {
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testPredicateFailureException()
    {
        o = new PredicateFailureException();
        assertEquals(null, o.getMessage());
    }

    @Test
    void testPredicateFailureExceptionString()
    {
        o = new PredicateFailureException("My message");
        assertEquals("My message", o.getMessage());
    }

}
