package com.taitl.exlogic.unused.indexes;

import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.indexes.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.collections.Coll.*;
import static com.taitl.ex.examples.night_city.data.CityTestData.*;
import static org.junit.jupiter.api.Assertions.*;

class SetIndexTest
{
    SetIndex<String, Cat> cats_by_color;
    SetIndex<Location, Cat> cats_by_location = new SetIndex<>(c -> c.location);
    Cat cat;

    @BeforeEach
    void setUp()
    {
        cats_by_color = new SetIndex<>(c -> c.color);
        cats_by_color.add(GREY_CAT);
        cats_by_color.add(YELLOW_CAT);
        cats_by_color.add(BLACK_CAT);

        cats_by_location = new SetIndex<>(c -> c.location);
        cats_by_location.add(GREY_CAT);
        cats_by_location.add(YELLOW_CAT);
        cats_by_location.add(BLACK_CAT);
    }

    @AfterEach
    void tearDown()
    {
        cats_by_color = null;
    }

    @Test
    void testConstructor()
    {
        cats_by_location = new SetIndex<>(cat -> cat.location);
        assertTrue(cats_by_location instanceof Map);
        cats_by_location.add(GREY_CAT.location, GREY_CAT);
        assertTrue(cats_by_location.containsKey(LOCATION_PARK));
        cats_by_location.add(BLACK_CAT);
        assertTrue(cats_by_location.containsKey(LOCATION_GARDEN));
        cats_by_color = new SetIndex<>(c -> c.location.toString());
        cats_by_color.add(CityTestData.ORANGE_CAT);
        assertTrue(cats_by_color.containsKey("Garden"));
        assertThrows(IllegalArgumentException.class, () -> new SetIndex<>(null));
    }

    @Test
    void testSize()
    {
        assertEquals(3, cats_by_color.size());
        cats_by_color.removeValue("Grey", GREY_CAT);
        assertEquals(2, cats_by_color.size());
        cats_by_color.clear();
        assertEquals(0, cats_by_color.size());
    }

    @Test
    void testGet()
    {
        assertEquals(GREY_CAT, getFirst(cats_by_color.get("Grey")));
        assertEquals(YELLOW_CAT, getFirst(cats_by_color.get("Yellow")));
        assertEquals(BLACK_CAT, getFirst(cats_by_color.get("Black")));
        assertNull(cats_by_color.get("non-existing"));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.get(null));
    }

    @Test
    void testContains()
    {
        assertTrue(cats_by_color.containsKey("Grey"));
        assertTrue(cats_by_color.containsKey("Yellow"));
        assertTrue(cats_by_color.containsKey("Black"));
        assertTrue(cats_by_color.contains("Black", BLACK_CAT));
        assertTrue(!cats_by_color.contains("Black", GREY_CAT));
        assertTrue(!cats_by_color.containsKey("non-existing"));
        assertTrue(cats_by_location.contains(LOCATION_PARK, GREY_CAT));
        assertTrue(cats_by_location.contains(LOCATION_PARK, YELLOW_CAT));
        assertTrue(cats_by_location.contains(LOCATION_GARDEN, BLACK_CAT));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.containsKey(null));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.contains(null, GREY_CAT));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.contains("Black", (Cat) null));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.contains("Black", (Predicate<Set<Cat>>) null));
    }

    @Nested
    class AdvancedCases
    {
        @Test
        @DisplayName("Test contains with predicate")
        void containsWithPredicate()
        {
            assertTrue(cats_by_color.contains("Grey", cats -> cats.size() == 1),
                    "Contains exactly one element under key");
            assertFalse(cats_by_color.contains("Yellow", cats -> cats.size() > 1), "Contains 2 or more elements");
            assertTrue(cats_by_color.contains("Black", cats -> cats.contains(BLACK_CAT)),
                    "Contains specific element");
            assertTrue(!cats_by_color.contains("non-existing", cats -> true));
            assertThrows(IllegalArgumentException.class, () -> cats_by_color.contains("Grey", (Cat) null));
            assertThrows(IllegalArgumentException.class,
                    () -> cats_by_color.contains("Grey", (Predicate<Set<Cat>>) null));
        }

        @Test
        @DisplayName("Test rekey uses value equality")
        void rekeyUsesValueEquality()
        {
            SetIndex<String, Cat> index = new SetIndex<>(cat -> new String(cat.color));
            Cat cat = CityTestData.BLACK_CAT;
            index.add("Black", cat);
            assertDoesNotThrow(() -> index.reindex("Black", new String("Black"), cat));
            assertTrue(index.contains("Black", cat));
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> index.reindex("Black", "Orange", cat));
            assertTrue(ex.getMessage().contains("newKey"));
        }
    }

    @Test
    void testAdd()
    {
        cats_by_location = new SetIndex<>(cat -> cat.location);
        cats_by_location.add(LOCATION_PARK, GREY_CAT);
        assertTrue(cats_by_location.containsKey(LOCATION_PARK), "Add using explicit key");
        cats_by_location.add(BLACK_CAT);
        assertTrue(cats_by_location.containsKey(LOCATION_GARDEN),
                "Add using key derived from object (cat.location)");
        assertThrows(IllegalArgumentException.class, () -> cats_by_location.add(null));
        assertThrows(IllegalArgumentException.class, () -> cats_by_location.add(null, BLACK_CAT));
        assertThrows(IllegalArgumentException.class, () -> cats_by_location.add(LOCATION_PARK, null));
    }

    @Test
    void testGetKeyReturnsNull()
    {
        SetIndex<String, Cat> index = new SetIndex<>(cat -> null);
        assertThrows(IllegalArgumentException.class, () -> index.add(CityTestData.ORANGE_CAT));
    }

    @Test
    void testAddAll()
    {
        SetIndex<String, Cat> index = new SetIndex<>(c -> c.color);
        index.addAll(List.of(GREY_CAT, BLACK_CAT));

        assertTrue(index.contains("Grey", GREY_CAT));
        assertTrue(index.contains("Black", BLACK_CAT));
        assertThrows(IllegalArgumentException.class, () -> index.addAll(null));
    }

    @Test
    void testReindexUsingExtractor()
    {
        Cat orange = new Cat("Orange", "Garden");
        cats_by_location.add(orange);
        orange.location = LOCATION_PARK;
        cats_by_location.reindex(LOCATION_GARDEN, orange);

        assertFalse(cats_by_location.contains(LOCATION_GARDEN, orange));
        assertTrue(cats_by_location.contains(LOCATION_PARK, orange));
    }

    @Test
    void testIndex()
    {
        Cat orange = new Cat("Orange", "Garden");
        cats_by_location.index(null, orange);
        assertTrue(cats_by_location.contains(LOCATION_GARDEN, orange));

        cats_by_location.index(orange, null);
        assertFalse(cats_by_location.contains(LOCATION_GARDEN, orange));
    }

    @Test
    void testRemove()
    {
        assertEquals(BLACK_CAT, cats_by_location.removeValue(LOCATION_GARDEN, BLACK_CAT),
                "Remove single element / unique key");
        assertNull(cats_by_location.removeValue(LOCATION_GARDEN, BLACK_CAT), "Try removing second time");
        assertTrue(!cats_by_location.containsKey(LOCATION_GARDEN), "The key is not present anymore");
        assertEquals(GREY_CAT, cats_by_location.removeValue(LOCATION_PARK, GREY_CAT),
                "Remove element at a non-unique key");
        assertTrue(cats_by_location.containsKey(LOCATION_PARK), "Assert the key still present");
        assertTrue(cats_by_location.contains(LOCATION_PARK, cats -> cats.contains(YELLOW_CAT)),
                "Assert another value still present under key");
        assertEquals(YELLOW_CAT,
                getFirst(cats_by_location.remove(LOCATION_PARK, cat -> cat == YELLOW_CAT)),
                "Remove another element from non-unique key");
        assertTrue(!cats_by_location.containsKey(LOCATION_PARK), "The key is not present anymore");
        assertNull(cats_by_color.removeValue("Non-existing-key", BLACK_CAT),
                "Try removing a key that is not present");
        assertNull(cats_by_color.removeValue("Grey", BLACK_CAT),
                "Try removing a object that is not present");
        assertNull(cats_by_color.remove("Grey", cat -> cat == BLACK_CAT),
                "Try removing a object that is not present");
        assertThrows(IllegalArgumentException.class, () -> cats_by_location.removeValue(null, BLACK_CAT));
        assertThrows(IllegalArgumentException.class,
                () -> cats_by_location.removeValue(LOCATION_GARDEN, (Cat) null));
        assertThrows(IllegalArgumentException.class,
                () -> cats_by_location.remove(LOCATION_GARDEN, (Predicate<Cat>) null));
        assertThrows(IllegalArgumentException.class,
                () -> cats_by_location.remove(null, cat -> true));
    }

    @Test
    @DisplayName("Test get by object key")
    void testGetByObjectKey()
    {
        assertEquals(GREY_CAT, getFirst(cats_by_color.get("Grey")));
        assertThrows(IllegalArgumentException.class,
                () -> cats_by_location.get(null));
        assertNull(cats_by_location.get(BLACK_CAT));
        cats_by_color.clear();
        assertNull(cats_by_color.get("Grey"));
        cats_by_color.add(ORANGE_CAT);
        assertEquals(ORANGE_CAT, getFirst(cats_by_color.get("Orange")));
        assertNull(cats_by_color.get(BLACK_CAT));
    }

}
