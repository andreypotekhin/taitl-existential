package com.taitl.existential.helper;

import com.taitl.ex.common.helper.*;
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
            Args.sane(o, "arg");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Args.sane(null, "arg");
        });
        assertDoesNotThrow(() -> {
            Args.sane(o, "arg1", o, "arg2");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Args.sane(null, "arg1", o, "arg2");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Args.sane(o, "arg1", null, "arg2");
        });
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.sane(o, "arg1", false);
        }).getMessage(), containsString("must be of even length"));
    }

    @Test
    void require()
    {
        assertDoesNotThrow(() -> {
            Args.check(true, "message");
        });
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.check(false, "message");
        }).getMessage(), is("message"));
        assertDoesNotThrow(() -> {
            Args.check(true, "msg1", true, "msg2");
        });
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.check(false, "msg1", o, "msg2");
        }).getMessage(), is("msg1"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.check(true, "msg1", false, "msg2");
        }).getMessage(), is("msg2"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.check(true, "msg1", true);
        }).getMessage(), containsString("must be of even length"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.check(true, "msg1", o, "msg2");
        }).getMessage(), containsString("must be boolean"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.check(true, "msg1", false);
        }).getMessage(), containsString("must be of even length"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            Args.check(true, "msg1", (Object[]) null);
        }).getMessage(), containsString("must not be null"));
    }
}
