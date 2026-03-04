package com.taitl.existential.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;

class PredicateFailureTest
{
    @Nested
    class Constructor
    {
        @Test
        @DisplayName("Default constructor leaves message null")
        void defaultMessage()
        {
            PredicateFailure error = new PredicateFailure();
            assertEquals(null, error.getMessage());
        }

        @Test
        @DisplayName("String constructor keeps message")
        void customMessage()
        {
            PredicateFailure error = new PredicateFailure("My message");
            assertEquals("My message", error.getMessage());
        }
    }
}
