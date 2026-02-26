package com.taitl.ex.logic.events.logic;

import com.taitl.ex.logic.evaluation.logic.SplitByGenericsDimension;
import com.taitl.existential.keys.TypeKey;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SplitByGenericsDimensionTest
{
    @Test
    void splitSingleDimension()
    {
        SplitByGenericsDimension splitter = new SplitByGenericsDimension();

        Set<String> keys = splitter.split(TypeKey.valueOf("T<A<X>>"))
                .stream()
                .map(TypeKey::toString)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        assertEquals(Set.of("T<A<X>>", "T<A<?>>", "T<A>"), keys);
    }

    @Test
    void splitMultipleDimensions()
    {
        SplitByGenericsDimension splitter = new SplitByGenericsDimension();

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
