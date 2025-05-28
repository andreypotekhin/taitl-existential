package com.taitl.existential.helper;

import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class ArgsTest
{
    Object o = "object";

    @Test
    void cool()
    {
        assertDoesNotThrow(() -> {
            Args.cool(o, "arg");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Args.cool(null, "arg");
        });
        assertDoesNotThrow(() -> {
            Args.cool(o, "arg1", o, "arg2");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Args.cool(null, "arg1", o, "arg2");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Args.cool(o, "arg1", null, "arg2");
        });
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.cool(o, "arg1", false);
        }).getMessage(), containsString("must be of even length"));
    }

    @Test
    void require()
    {
        assertDoesNotThrow(() -> {
            Args.require(true, "message");
        });
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.require(false, "message");
        }).getMessage(), is("message"));
        assertDoesNotThrow(() -> {
            Args.require(true, "msg1", true, "msg2");
        });
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.require(false, "msg1", o, "msg2");
        }).getMessage(), is("msg1"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.require(true, "msg1", false, "msg2");
        }).getMessage(), is("msg2"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.require(true, "msg1", true);
        }).getMessage(), containsString("must be of even length"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.require(true, "msg1", o, "msg2");
        }).getMessage(), containsString("must be boolean"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.require(true, "msg1", false);
        }).getMessage(), containsString("must be of even length"));
    }
}