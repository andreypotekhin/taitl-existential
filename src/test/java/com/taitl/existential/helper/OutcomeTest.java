package com.taitl.existential.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OutcomeTest
{
    Object o = "object";

    @Test
    void cool()
    {
        assertDoesNotThrow(() -> {
            Outcome.cool(o, "arg");
        });
        assertThrows(RuntimeException.class, () -> {
            Outcome.cool(null, "arg");
        });
    }

    @Test
    void verify()
    {
        assertDoesNotThrow(() -> {
            Outcome.verify(true, "message");
        });
        assertThrows(RuntimeException.class, () -> {
            Outcome.verify(false, "message");
        });
    }
}