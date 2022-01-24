package com.taitl.existential.indexes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.taitl.existential.model.Cat;
import com.taitl.existential.model.Location;
import com.taitl.existential.test_data.TestData;

class IndexTest
{
    Index<Cat, String> o;

    @BeforeEach
    void setUp() throws Exception
    {
        o = new Index<>(c -> c.color);
        o.add(TestData.GREY_CAT);
        o.add(TestData.YELLOW_CAT);
        o.add(TestData.BLACK_CAT);        
    }

    @AfterEach
    void tearDown() throws Exception
    {
        o = null;
    }

    @Test
    void testConstructor()
    {
        Index<Cat, Location> cats_by_location = new Index<>();
        o.add(TestData.GREY_CAT, TestData.GREY_CAT.location);
        Index<Cat, Location> cats_by_location = new Index<>();
        new Index<Cat, String>(c -> c.location.toString());
        assertThrows(IllegalArgumentException.class, () -> new Index<>(null));
    }
    
    @Test
    void testGet()
    {
        assertSame(TestData.GREY_CAT, o.get("Grey").stream().findFirst().get());
        assertSame(TestData.YELLOW_CAT, o.get("Yellow").stream().findFirst().get());
        assertSame(TestData.BLACK_CAT, o.get("Black").stream().findFirst().get());
        assertNull(o.get("non-existing"));
        assertThrows(IllegalArgumentException.class, () -> o.get(null));
    }

    @Test
    void testContains()
    {
        assertTrue(o.contains("Grey"));
        assertTrue(o.contains("Yellow"));
        assertTrue(o.contains("Black"));
        assertTrue(!o.contains("non-existing"));
        assertThrows(IllegalArgumentException.class, () -> o.contains(null));
    }
    
    @Test
    void testContainsWithPredicate()
    {
        assertTrue(o.contains("Grey", cats -> cats.size() == 1), "Contains exactly one element under key");
        assertFalse(o.contains("Yellow", cats -> cats.size() > 1), "Contains 2 or more elements");
        assertTrue(o.contains("Black", cats -> cats.contains(TestData.BLACK_CAT)), "Contains specific element");
        assertTrue(!o.contains("non-existing", cats -> true));
        assertThrows(IllegalArgumentException.class, () -> o.contains("Grey", null));
    }

}
