package com.taitl.existential.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

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
        assertThrows(IllegalArgumentException.class, () -> {
            Args.require(false, "message");
        });
    }
}