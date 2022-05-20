package com.taitl.existential.utilities;

import static com.taitl.existential.model.cats.TestData.BLACK_CAT;
import static com.taitl.existential.model.cats.TestData.GREY_CAT;
import static com.taitl.existential.model.cats.TestData.LOCATION_GARDEN;
import static com.taitl.existential.model.cats.TestData.LOCATION_PARK;
import static com.taitl.existential.model.cats.TestData.ORANGE_CAT;
import static com.taitl.existential.model.cats.TestData.YELLOW_CAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.taitl.existential.model.cats.Cat;
import com.taitl.existential.model.cats.Location;
import com.taitl.existential.utilities.Multimap;

class MultimapTest
{
    Multimap<Location, Cat> o;
    Set<Cat> cats;

    @BeforeEach
    void setUp() throws Exception
    {
        o = new Multimap<Location, Cat>();
        o.put(LOCATION_PARK, GREY_CAT);
        o.put(LOCATION_PARK, YELLOW_CAT);
    }

    @AfterEach
    void tearDown() throws Exception
    {
        o = null;
    }

    @Test
    void testGet()
    {
        assertThrows(IllegalArgumentException.class, () -> o.get(null));

        cats = o.get(LOCATION_PARK);
        assertEquals(2, cats.size());

        cats = o.get(LOCATION_GARDEN);
        assertNull(cats);

    }

    @Test
    void testPut()
    {
        assertThrows(IllegalArgumentException.class, () -> o.put(null, GREY_CAT));
        assertThrows(IllegalArgumentException.class, () -> o.put(LOCATION_PARK, null));

        o.put(LOCATION_PARK, BLACK_CAT);
        o.put(LOCATION_GARDEN, BLACK_CAT);
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
        assertThrows(IllegalArgumentException.class, () -> o.remove(null, GREY_CAT));
        assertThrows(IllegalArgumentException.class, () -> o.remove(LOCATION_PARK, (Cat) null));
        assertThrows(IllegalArgumentException.class, () -> o.remove(LOCATION_PARK, (Predicate<Cat>) null));

        assertEquals(YELLOW_CAT, o.remove(LOCATION_PARK, YELLOW_CAT));
        assertEquals(1, o.get(LOCATION_PARK).size());
        assertNull(o.remove(LOCATION_PARK, ORANGE_CAT));
        assertEquals(1, o.get(LOCATION_PARK).size());
        assertEquals(GREY_CAT, o.remove(LOCATION_PARK, GREY_CAT));
        assertEquals(0, o.get(LOCATION_PARK).size());
        assertNull(o.remove(LOCATION_PARK, GREY_CAT));
        assertEquals(0, o.get(LOCATION_PARK).size());
        assertNull(o.get(LOCATION_GARDEN));
    }

    @Test
    void testContainsKey()
    {
        assertThrows(IllegalArgumentException.class, () -> o.containsKey(null));

        assertTrue(o.containsKey(LOCATION_PARK));
        o.remove(LOCATION_PARK, YELLOW_CAT);
        assertTrue(o.containsKey(LOCATION_PARK));
        o.remove(LOCATION_PARK, GREY_CAT);
        assertTrue(!o.containsKey(LOCATION_PARK));

        assertTrue(!o.containsKey(LOCATION_GARDEN));
        o.put(LOCATION_GARDEN, GREY_CAT);
        assertTrue(o.containsKey(LOCATION_GARDEN));
        o.remove(LOCATION_GARDEN, GREY_CAT);
        assertTrue(!o.containsKey(LOCATION_GARDEN));
    }

    @Test
    void testSize()
    {
        assertEquals(1, o.size());
        o.remove(LOCATION_PARK, GREY_CAT);
        assertEquals(1, o.size());
        o.remove(LOCATION_PARK, YELLOW_CAT);
        assertEquals(0, o.size());
    }

    @Test
    void testClear()
    {
        o.clear();
        assertEquals(0, o.size());
    }
}
