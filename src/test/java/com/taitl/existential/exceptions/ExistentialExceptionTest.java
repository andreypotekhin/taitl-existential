package com.taitl.existential.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExistentialExceptionTest
{
    ExistentialException o;

    @BeforeEach
    void setUp()
    {
    }

    @AfterEach
    void tearDown()
    {
    }

    @Test
    @DisplayName("Test failure exception")
    void testFailureException()
    {
        o = new ExistentialException();
        assertEquals(null, o.getMessage());
    }

    @Test
    @DisplayName("Test failure exception string")
    void testFailureExceptionString()
    {
        o = new ExistentialException("My message");
        assertEquals("My message", o.getMessage());
    }

}
