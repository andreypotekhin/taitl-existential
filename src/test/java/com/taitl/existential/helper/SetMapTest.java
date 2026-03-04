package com.taitl.existential.helper;

import com.taitl.ex.common.helper.collections.SetMap;
import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.ex.examples.night_city.model.Location;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.taitl.ex.examples.night_city.data.CityTestData.*;
import static org.junit.jupiter.api.Assertions.*;

class SetMapTest
{
    SetMap<Location, Cat> o;
    Set<Cat> cats;

    @BeforeEach
    void setUp()
    {
        o = new SetMap<Location, Cat>();
        o.add(LOCATION_PARK, GREY_CAT);
        o.add(LOCATION_PARK, YELLOW_CAT);
    }

    @AfterEach
    void tearDown()
    {
        o = null;
    }

    @Test
    void testGet()
    {
        assertThrows(IllegalArgumentException.class, () -> o.get(null));

        cats = o.get(LOCATION_PARK);
        assertEquals(2, cats.size());
        assertThrows(UnsupportedOperationException.class, () -> cats.add(BLACK_CAT));

        cats = o.get(LOCATION_GARDEN);
        assertNull(cats);

    }

    @Test
    void testPut()
    {
        assertThrows(IllegalArgumentException.class, () -> o.add(null, GREY_CAT));
        assertThrows(IllegalArgumentException.class, () -> o.add(LOCATION_PARK, null));

        Set<Cat> returned = o.add(LOCATION_PARK, BLACK_CAT);
        assertThrows(UnsupportedOperationException.class, () -> returned.add(ORANGE_CAT));
        o.add(LOCATION_GARDEN, BLACK_CAT);
        // We can see that same value can be present under different keys.
        // This not very well match the real life, since cat now present in
        // two locations. Class Multimap does not enforce a single presence
        // rule; for that, a different structure exists (Index).
        cats = o.get(LOCATION_PARK);
        assertEquals(3, cats.size());
        cats = o.get(LOCATION_GARDEN);
        assertEquals(1, cats.size());
    }

    @Test
    void testRemove()
    {
        assertThrows(IllegalArgumentException.class, () -> o.removeValue(null, GREY_CAT));
        assertThrows(IllegalArgumentException.class, () -> o.removeValue(LOCATION_PARK, (Cat) null));
        assertThrows(IllegalArgumentException.class, () -> o.removeMatching(LOCATION_PARK, (Predicate<Cat>) null));

        assertEquals(YELLOW_CAT, o.removeValue(LOCATION_PARK, YELLOW_CAT));
        assertEquals(1, o.get(LOCATION_PARK).size());
        assertNull(o.removeValue(LOCATION_PARK, ORANGE_CAT));
        assertEquals(1, o.get(LOCATION_PARK).size());
        assertEquals(GREY_CAT, o.removeValue(LOCATION_PARK, GREY_CAT));
        assertNull(o.get(LOCATION_PARK));
        assertNull(o.removeValue(LOCATION_PARK, GREY_CAT));
        assertNull(o.get(LOCATION_PARK));
        assertNull(o.get(LOCATION_GARDEN));
    }

    @Test
    @DisplayName("Supports Map API")
    void supportsMapApi()
    {
        Map<Location, Set<Cat>> map = o;

        Set<Cat> replacement = new LinkedHashSet<>();
        replacement.add(BLACK_CAT);

        Set<Cat> previous = map.put(LOCATION_PARK, replacement);
        assertEquals(2, previous.size());
        assertEquals(1, map.get(LOCATION_PARK).size());
        assertTrue(map.containsKey(LOCATION_PARK));

        Set<Cat> removed = map.remove(LOCATION_PARK);
        assertEquals(1, removed.size());
        assertNull(map.get(LOCATION_PARK));
        assertTrue(map.isEmpty());
    }

    @Nested
    class RemovePredicate
    {
        @Test
        @DisplayName("Test remove predicate evaluated once")
        void evaluatedOnce()
        {
            AtomicInteger calls = new AtomicInteger();
            Set<Cat> removed = o.removeMatching(LOCATION_PARK, cat -> {
                calls.incrementAndGet();
                return false;
            });
            assertNull(removed);
            assertEquals(2, calls.get());
            assertEquals(2, o.get(LOCATION_PARK).size());
        }
    }

    @Test
    @DisplayName("Test contains key")
    void testContainsKey()
    {
        assertThrows(IllegalArgumentException.class, () -> o.containsKey(null));

        assertTrue(o.containsKey(LOCATION_PARK));
        o.removeValue(LOCATION_PARK, YELLOW_CAT);
        assertTrue(o.containsKey(LOCATION_PARK));
        o.removeValue(LOCATION_PARK, GREY_CAT);
        assertTrue(!o.containsKey(LOCATION_PARK));

        assertTrue(!o.containsKey(LOCATION_GARDEN));
        o.add(LOCATION_GARDEN, GREY_CAT);
        assertTrue(o.containsKey(LOCATION_GARDEN));
        o.removeValue(LOCATION_GARDEN, GREY_CAT);
        assertTrue(!o.containsKey(LOCATION_GARDEN));
    }

    @Test
    void testSize()
    {
        assertEquals(1, o.size());
        o.removeValue(LOCATION_PARK, GREY_CAT);
        assertEquals(1, o.size());
        o.removeValue(LOCATION_PARK, YELLOW_CAT);
        assertEquals(0, o.size());
    }

    @Test
    void testClear()
    {
        o.clear();
        assertEquals(0, o.size());
    }
}
