package com.taitl.existential.helper;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
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
            State.cool(o, "val");
        });
        assertThrows(IllegalStateException.class, () -> {
            State.cool(null, "val");
        });
        assertDoesNotThrow(() -> {
            State.cool(o, "val1", o, "val2");
        });
        assertThat(assertThrows(IllegalStateException.class, () -> {
            State.cool(null, "val1", o, "val2");
        }).getMessage(), containsString("'val1' must not be null"));
        assertThat(assertThrows(IllegalStateException.class, () -> {
            State.cool(o, "val1", null, "val2");
        }).getMessage(), containsString("'val2' must not be null"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            State.cool(o, "val1", false);
        }).getMessage(), containsString("must be of even length"));
    }

    @Test
    void verify()
    {
        assertDoesNotThrow(() -> {
            State.verify(true, "message");
        });
        assertThat(assertThrows(IllegalStateException.class, () -> {
            State.verify(false, "message");
        }).getMessage(), is("message"));
        assertDoesNotThrow(() -> {
            State.verify(true, "msg1", true, "msg2");
        });
        assertThat(assertThrows(IllegalStateException.class, () -> {
            State.verify(false, "msg1", o, "msg2");
        }).getMessage(), is("msg1"));
        assertThat(assertThrows(IllegalStateException.class, () -> {
            State.verify(true, "msg1", false, "msg2");
        }).getMessage(), is("msg2"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            State.verify(true, "msg1", true);
        }).getMessage(), containsString("must be of even length"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            State.verify(true, "msg1", o, "msg2");
        }).getMessage(), containsString("must be boolean"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            State.verify(true, "msg1", false);
        }).getMessage(), containsString("must be of even length"));
    }
}