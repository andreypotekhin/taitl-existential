package com.taitl.existential.helper;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OutcomeTest
{
    Object o = "object";

    @Test
    void cool()
    {
        assertDoesNotThrow(() -> {
            Outcome.cool(o, "val");
        });
        assertThrows(RuntimeException.class, () -> {
            Outcome.cool(null, "val");
        });
        assertDoesNotThrow(() -> {
            Outcome.cool(o, "val1", o, "val2");
        });
        assertThat(assertThrows(RuntimeException.class, () -> {
            Outcome.cool(null, "val1", o, "val2");
        }).getMessage(), containsString("'val1' must not be null"));
        assertThat(assertThrows(RuntimeException.class, () -> {
            Outcome.cool(o, "val1", null, "val2");
        }).getMessage(), containsString("'val2' must not be null"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Outcome.cool(o, "val1", false);
        }).getMessage(), containsString("must be of even length"));
    }

    @Test
    void verify()
    {
        assertDoesNotThrow(() -> {
            Outcome.verify(true, "message");
        });
        assertThat(assertThrows(RuntimeException.class, () -> {
            Outcome.verify(false, "message");
        }).getMessage(), is("message"));
        assertDoesNotThrow(() -> {
            Outcome.verify(true, "msg1", true, "msg2");
        });
        assertThat(assertThrows(RuntimeException.class, () -> {
            Outcome.verify(false, "msg1", o, "msg2");
        }).getMessage(), is("msg1"));
        assertThat(assertThrows(RuntimeException.class, () -> {
            Outcome.verify(true, "msg1", false, "msg2");
        }).getMessage(), is("msg2"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Outcome.verify(true, "msg1", true);
        }).getMessage(), containsString("must be of even length"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Outcome.verify(true, "msg1", o, "msg2");
        }).getMessage(), containsString("must be boolean"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Outcome.verify(true, "msg1", false);
        }).getMessage(), containsString("must be of even length"));
    }
}