package com.taitl.ex.common.helper.collections;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ListMapTest
{
    @Nested
    @DisplayName("putList")
    class PutList
    {
        @Test
        @DisplayName("adjusts size when list contents change")
        void adjustsSize()
        {
            ListMap<String, String> map = new ListMap<>();
            assertEquals(0, map.size());
            map.putList("a", List.of("x"));
            assertEquals(1, map.size());
            assertEquals(List.of("x"), map.get("a"));
            map.putList("a", List.of("y", "z"));
            assertEquals(1, map.size());
            assertEquals(List.of("y", "z"), map.get("a"));
            map.putList("a", List.of());
            assertEquals(0, map.size());
            assertEquals(List.of(), map.get("a"));
        }

        @Test
        @DisplayName("keeps size zero for empty list on new key")
        void keepsZeroSize()
        {
            ListMap<String, String> map = new ListMap<>();
            assertEquals(0, map.size());
            map.putList("a", List.of());
            assertEquals(0, map.size());
            map.putList("a", List.of("x"));
            assertEquals(1, map.size());
        }
    }
}
