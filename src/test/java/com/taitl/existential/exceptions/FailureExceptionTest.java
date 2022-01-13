package com.taitl.existential.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FailureExceptionTest
{
    FailureException o;

    @BeforeEach
    void setUp() throws Exception
    {
    }

    @AfterEach
    void tearDown() throws Exception
    {
    }

    @Test
    void testFailureException()
    {
        o = new FailureException();
        assertEquals(null, o.getMessage());
    }

    @Test
    void testFailureExceptionString()
    {
        o = new FailureException("My message");
        assertEquals("My message", o.getMessage());
    }

}
