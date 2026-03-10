package com.taitl.ex.common.helper.collections;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ListMapTest
{
    @Test
    @DisplayName("add appends values under the same key")
    void add()
    {
        ListMap<String, String> map = new ListMap<>();
        map.add("a", "x");
        map.add("a", "y");

        assertEquals(List.of("x", "y"), map.get("a"));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("put replaces list and supports Map API")
    void supportsMapApi()
    {
        ListMap<String, String> listMap = new ListMap<>();
        listMap.add("a", "x");

        Map<String, List<String>> map = listMap;
        List<String> previous = map.put("a", List.of("y", "z"));

        assertEquals(List.of("x"), previous);
        assertEquals(List.of("y", "z"), map.get("a"));
        assertTrue(map.containsKey("a"));

        List<String> removed = map.remove("a");
        assertEquals(List.of("y", "z"), removed);
        assertFalse(map.containsKey("a"));
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("put with empty list removes key and keeps size consistent")
    void putEmptyListRemovesKey()
    {
        ListMap<String, String> map = new ListMap<>();
        map.add("a", "x");

        List<String> previous = map.put("a", List.of());
        assertEquals(List.of("x"), previous);
        assertNull(map.get("a"));
        assertFalse(map.containsKey("a"));
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("removeValue and removeMatching remove key when list becomes empty")
    void removeHelpers()
    {
        ListMap<String, String> map = new ListMap<>();
        map.add("a", "x");
        map.add("a", "y");

        assertEquals("x", map.removeValue("a", "x"));
        assertEquals(List.of("y"), map.get("a"));

        assertEquals(List.of("y"), map.removeMatching("a", v -> true));
        assertNull(map.get("a"));
        assertEquals(0, map.size());
    }

}
