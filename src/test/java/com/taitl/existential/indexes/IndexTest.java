package com.taitl.existential.indexes;

import static com.taitl.existential.helper.CollectionUtils.getFirst;
import static com.taitl.existential.model.cats.TestData.BLACK_CAT;
import static com.taitl.existential.model.cats.TestData.GREY_CAT;
import static com.taitl.existential.model.cats.TestData.LOCATION_GARDEN;
import static com.taitl.existential.model.cats.TestData.LOCATION_PARK;
import static com.taitl.existential.model.cats.TestData.ORANGE_CAT;
import static com.taitl.existential.model.cats.TestData.YELLOW_CAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.taitl.existential.model.cats.TestData;

class IndexTest
{
    Index<String, Cat> cats_by_color;
    Index<Location, Cat> cats_by_location = new Index<>();
    Cat cat;

    @BeforeEach
    void setUp() throws Exception
    {
        cats_by_color = new Index<>(c -> c.color);
        cats_by_color.add(GREY_CAT);
        cats_by_color.add(YELLOW_CAT);
        cats_by_color.add(BLACK_CAT);

        cats_by_location = new Index<>(c -> c.location);
        cats_by_location.add(GREY_CAT);
        cats_by_location.add(YELLOW_CAT);
        cats_by_location.add(BLACK_CAT);
    }

    @AfterEach
    void tearDown() throws Exception
    {
        cats_by_color = null;
    }

    @Test
    void testConstructor()
    {
        cats_by_location = new Index<>();
        cats_by_location.add(GREY_CAT.location, GREY_CAT);
        assertTrue(cats_by_location.contains(LOCATION_PARK));
        cats_by_location = new Index<>(cat -> cat.location);
        cats_by_location.add(BLACK_CAT);
        assertTrue(cats_by_location.contains(LOCATION_GARDEN));
        cats_by_color = new Index<>(c -> c.location.toString());
        cats_by_color.add(TestData.ORANGE_CAT);
        assertTrue(cats_by_color.contains("Garden"));
        assertThrows(IllegalArgumentException.class, () -> new Index<>(null));
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
        assertTrue(cats_by_color.contains("Grey"));
        assertTrue(cats_by_color.contains("Yellow"));
        assertTrue(cats_by_color.contains("Black"));
        assertTrue(cats_by_color.contains("Black", BLACK_CAT));
        assertTrue(!cats_by_color.contains("Black", GREY_CAT));
        assertTrue(!cats_by_color.contains("non-existing"));
        assertTrue(cats_by_location.contains(LOCATION_PARK, GREY_CAT));
        assertTrue(cats_by_location.contains(LOCATION_PARK, YELLOW_CAT));
        assertTrue(cats_by_location.contains(LOCATION_GARDEN, BLACK_CAT));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.contains(null));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.contains(null, GREY_CAT));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.contains("Black", (Cat) null));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.contains("Black", (Predicate<Set<Cat>>) null));
    }

    @Test
    void testContainsWithPredicate()
    {
        assertTrue(cats_by_color.contains("Grey", cats -> cats.size() == 1), "Contains exactly one element under key");
        assertFalse(cats_by_color.contains("Yellow", cats -> cats.size() > 1), "Contains 2 or more elements");
        assertTrue(cats_by_color.contains("Black", cats -> cats.contains(BLACK_CAT)),
                "Contains specific element");
        assertTrue(!cats_by_color.contains("non-existing", cats -> true));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.contains("Grey", (Cat) null));
        assertThrows(IllegalArgumentException.class, () -> cats_by_color.contains("Grey", (Predicate<Set<Cat>>) null));
    }

    @Test
    void testAdd()
    {
        cats_by_location = new Index<>(cat -> cat.location);
        cats_by_location.add(LOCATION_PARK, GREY_CAT);
        assertTrue(cats_by_location.contains(LOCATION_PARK), "Add using explicit key");
        cats_by_location.add(BLACK_CAT);
        assertTrue(cats_by_location.contains(LOCATION_GARDEN),
                "Add using key derived from object (cat.location)");
        assertThrows(IllegalArgumentException.class, () -> cats_by_location.add(null));
        assertThrows(IllegalArgumentException.class, () -> cats_by_location.add(null, BLACK_CAT));
        assertThrows(IllegalArgumentException.class, () -> cats_by_location.add(LOCATION_PARK, null));
        Index<String, Cat> index_without_get_index_function = new Index<>();
        index_without_get_index_function.add("Grey", TestData.GREY_CAT);
        assertTrue(index_without_get_index_function.contains("Grey", TestData.GREY_CAT));
        assertThrows(IllegalArgumentException.class, () -> index_without_get_index_function.add(TestData.ORANGE_CAT));
    }

    @Test
    void testRemove()
    {
        assertEquals(BLACK_CAT, cats_by_location.remove(LOCATION_GARDEN, BLACK_CAT),
                "Remove single element / unique key");
        assertNull(cats_by_location.remove(LOCATION_GARDEN, BLACK_CAT), "Try removing second time");
        assertTrue(!cats_by_location.contains(LOCATION_GARDEN), "The key is not present anymore");
        assertEquals(GREY_CAT, cats_by_location.remove(LOCATION_PARK, GREY_CAT),
                "Remove element at a non-unique key");
        assertTrue(cats_by_location.contains(LOCATION_PARK), "Assert the key still present");
        assertTrue(cats_by_location.contains(LOCATION_PARK, cats -> cats.contains(YELLOW_CAT)),
                "Assert another value still present under key");
        assertEquals(YELLOW_CAT,
                getFirst(cats_by_location.remove(LOCATION_PARK, cat -> cat == YELLOW_CAT)),
                "Remove another element from non-unique key");
        assertTrue(!cats_by_location.contains(LOCATION_PARK), "The key is not present anymore");
        assertNull(cats_by_color.remove("Non-existing-key", BLACK_CAT),
                "Try removing a key that is not present");
        assertNull(cats_by_color.remove("Grey", BLACK_CAT),
                "Try removing a object that is not present");
        assertNull(cats_by_color.remove("Grey", cat -> cat == BLACK_CAT),
                "Try removing a object that is not present");
        assertThrows(IllegalArgumentException.class, () -> cats_by_location.remove(null, BLACK_CAT));
        assertThrows(IllegalArgumentException.class,
                () -> cats_by_location.remove(LOCATION_GARDEN, (Cat) null));
        assertThrows(IllegalArgumentException.class,
                () -> cats_by_location.remove(LOCATION_GARDEN, (Predicate<Cat>) null));
        assertThrows(IllegalArgumentException.class,
                () -> cats_by_location.remove(null, cat -> true));
    }

    @Test
    void testGetObj()
    {
        assertEquals(GREY_CAT, getFirst(cats_by_color.getObj("Grey")));
        assertThrows(IllegalArgumentException.class,
                () -> cats_by_location.getObj(null));
        assertThrows(IllegalArgumentException.class,
                () -> cats_by_location.getObj(BLACK_CAT), "Wrong type of key");
        cats_by_color.clear();
        assertNull(cats_by_color.getObj("Grey"));
        cats_by_color.add(ORANGE_CAT);
        assertEquals(ORANGE_CAT, getFirst(cats_by_color.getObj("Orange")));
        assertThrows(IllegalArgumentException.class,
                () -> cats_by_color.getObj(BLACK_CAT), "Wrong type of key");
    }
}
