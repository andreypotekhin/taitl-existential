package com.taitl.existential.helper;

import com.taitl.ex.common.helper.collections.ConcurrentSetMap;
import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.ex.examples.night_city.model.Location;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.Map;
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
        o.add(LOCATION_PARK, GREY_CAT);
        o.add(LOCATION_PARK, YELLOW_CAT);
    }

    @Nested
    class Snapshots
    {
        @Test
        @DisplayName("Get returns snapshot")
        void get()
        {
            Set<Cat> snapshot = o.get(LOCATION_PARK);
            assertEquals(2, snapshot.size());

            o.add(LOCATION_PARK, BLACK_CAT);
            assertEquals(2, snapshot.size());

            o.removeValue(LOCATION_PARK, GREY_CAT);
            assertEquals(2, snapshot.size());
        }

        @Test
        @DisplayName("Remove predicate returns snapshot")
        void removePredicate()
        {
            Set<Cat> removed = o.removeMatching(LOCATION_PARK, cat -> true);
            assertEquals(2, removed.size());
            assertThrows(UnsupportedOperationException.class, () -> removed.clear());
        }

        @Test
        @DisplayName("Map view methods return snapshots")
        void mapViews()
        {
            Set<Location> keys = o.keySet();
            Collection<Set<Cat>> values = o.values();
            Set<Map.Entry<Location, Set<Cat>>> entries = o.entrySet();

            assertEquals(1, keys.size());
            assertEquals(1, values.size());
            assertEquals(1, entries.size());

            o.add(LOCATION_GARDEN, BLACK_CAT);

            assertEquals(1, keys.size());
            assertEquals(1, values.size());
            assertEquals(1, entries.size());
            assertThrows(UnsupportedOperationException.class, keys::clear);
        }
    }
}
