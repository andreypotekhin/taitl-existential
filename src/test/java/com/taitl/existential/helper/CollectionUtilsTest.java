package com.taitl.existential.helper;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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