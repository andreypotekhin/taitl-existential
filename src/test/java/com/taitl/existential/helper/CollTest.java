package com.taitl.existential.helper;

import java.util.*;

import com.taitl.ex.common.helper.collections.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CollTest
{
    @Test
    void getFirst()
    {
        Collection<String> coll = new LinkedHashSet<>();
        coll.add("a");
        coll.add("b");
        coll.add("c");
        assertEquals("a", Coll.getFirst(coll));
    }
}