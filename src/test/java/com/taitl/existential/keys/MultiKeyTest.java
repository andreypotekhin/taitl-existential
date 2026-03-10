package com.taitl.existential.keys;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

class MultiKeyTest
{
    @Test
    @DisplayName("Joined key preserves order")
    void joinedKeyPreservesOrder()
    {
        List<EventKey<String>> keys = List.of(
                EventKey.valueOf("Create<String>"),
                EventKey.valueOf("Update<String>"),
                EventKey.valueOf("Delete<String>"));

        MultiKey<String> multiKey = MultiKey.valueOf(keys);

        assertThat(multiKey.toString(),
                is("Create<String>,Update<String>,Delete<String>"));
    }

    @Test
    @DisplayName("Equality and hash code by joined key")
    void equalityAndHashCode()
    {
        MultiKey<String> first = MultiKey.valueOf(List.of(
                EventKey.valueOf("Create<String>"),
                EventKey.valueOf("Update<String>")));
        MultiKey<String> second = MultiKey.valueOf(List.of(
                EventKey.valueOf("Create<String>"),
                EventKey.valueOf("Update<String>")));
        MultiKey<String> third = MultiKey.valueOf(List.of(
                EventKey.valueOf("Update<String>"),
                EventKey.valueOf("Create<String>")));

        assertThat(first.equals(first), is(true));
        assertThat(first.equals(second), is(true));
        assertThat(first.equals(third), is(false));
        assertThat(first.equals(null), is(false));
        assertThat(first.equals("Create<String>,Update<String>"), is(false));
        assertThat(first.hashCode(), is(second.hashCode()));
    }
}
