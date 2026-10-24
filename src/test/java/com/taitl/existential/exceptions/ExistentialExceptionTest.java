package com.taitl.existential.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.*;

class ExistentialExceptionTest
{
    @Test
    @DisplayName("Can be thrown without a throws declaration")
    void isUnchecked()
    {
        assertThrows(ExistentialException.class, ExistentialExceptionTest::throwUnchecked);
    }

    private static void throwUnchecked()
    {
        throw new ExistentialException();
    }

    @Nested
    class Constructor
    {
        @Test
        @DisplayName("Default constructor leaves message null")
        void defaultMessage()
        {
            ExistentialException error = new ExistentialException();
            assertEquals(null, error.getMessage());
        }

        @Test
        @DisplayName("String constructor keeps message")
        void customMessage()
        {
            ExistentialException error = new ExistentialException("My message");
            assertEquals("My message", error.getMessage());
        }
    }
}
