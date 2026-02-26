package com.taitl.existential.helper;

import com.taitl.ex.common.helper.collections.ConcurrentSetMap;
import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.ex.examples.night_city.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.taitl.ex.examples.night_city.data.CityTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConcurrentSetMapTest
{
    ConcurrentSetMap<Location, Cat> o;

    @BeforeEach
    void setUp()
    {
        o = new ConcurrentSetMap<Location, Cat>();
        o.put(LOCATION_PARK, GREY_CAT);
        o.put(LOCATION_PARK, YELLOW_CAT);
    }

    @Test
    void testGetReturnsSnapshot()
    {
        Set<Cat> snapshot = o.get(LOCATION_PARK);
        assertEquals(2, snapshot.size());

        o.put(LOCATION_PARK, BLACK_CAT);
        assertEquals(2, snapshot.size());

        o.remove(LOCATION_PARK, GREY_CAT);
        assertEquals(2, snapshot.size());
    }

    @Test
    void testRemovePredicateReturnsSnapshot()
    {
        Set<Cat> removed = o.remove(LOCATION_PARK, cat -> true);
        assertEquals(2, removed.size());
        assertThrows(UnsupportedOperationException.class, () -> removed.clear());
    }
}
