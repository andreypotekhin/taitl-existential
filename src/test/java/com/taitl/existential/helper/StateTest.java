package com.taitl.existential.helper;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateTest
{
    Object o = "object";

    @Test
    void cool()
    {
        assertDoesNotThrow(() -> {
            State.cool(o, "arg");
        });
        assertThrows(IllegalStateException.class, () -> {
            State.cool(null, "arg");
        });
    }

    @Test
    void verify()
    {
        assertDoesNotThrow(() -> {
            State.verify(true, "message");
        });
        assertThrows(IllegalStateException.class, () -> {
            State.verify(false, "message");
        });
    }
}