package com.taitl.ex.logic.events.logic;

import com.taitl.ex.logic.evaluation.actions.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

class SplitTypeKeyTest
{
    @Test
    void splitSingleDimension()
    {
        SplitTypeKey splitter = new SplitTypeKey();

        Set<String> keys = splitter.split(TypeKey.valueOf("T<A<X>>"))
                .stream()
                .map(TypeKey::toString)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        assertEquals(Set.of("T<A<X>>", "T<A<?>>", "T<A>"), keys);
    }

    @Test
    void splitMultipleDimensions()
    {
        SplitTypeKey splitter = new SplitTypeKey();

        Set<String> keys = splitter.split(TypeKey.valueOf("T<A<X>,B<Y>>"))
                .stream()
                .map(TypeKey::toString)
                .collect(Collectors.toSet());

        assertEquals(Set.of(
                "T<A<X>,B<Y>>",
                "T<A<X>,B<?>>",
                "T<A<X>,B>",
                "T<A<?>,B<Y>>",
                "T<A<?>,B<?>>",
                "T<A<?>,B>",
                "T<A,B<Y>>",
                "T<A,B<?>>",
                "T<A,B>"), keys);
    }
}
