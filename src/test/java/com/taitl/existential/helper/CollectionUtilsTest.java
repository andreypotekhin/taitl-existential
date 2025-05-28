package com.taitl.existential.helper;

import java.util.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilsTest
{
    @Test
    void getFirst()
    {
        Collection<String> coll = new LinkedHashSet<>();
        coll.add("a");
        coll.add("b");
        coll.add("c");
        assertEquals("a", CollectionUtils.getFirst(coll));
    }
}