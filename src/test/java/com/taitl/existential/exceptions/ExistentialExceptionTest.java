package com.taitl.existential.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExistentialExceptionTest
{
    ExistentialException o;

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
        o = new ExistentialException();
        assertEquals(null, o.getMessage());
    }

    @Test
    void testFailureExceptionString()
    {
        o = new ExistentialException("My message");
        assertEquals("My message", o.getMessage());
    }

}
