package com.taitl.ex.examples.night_city.model;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class EntityValueTest
{
    @Test
    void beingsCompareTypeColorAndLocation()
    {
        Cat cat = new Cat("Black", "Park");
        Cat same = new Cat("Black", "Park");
        assertEquals(cat, same);
        assertEquals(cat.hashCode(), same.hashCode());
        assertNotEquals(cat, new Cat("Black", "Garden"));
        assertNotEquals(cat, new Cat("Grey", "Park"));
        assertNotEquals(cat, new Mouse("Black", "Park"));
        assertNotEquals(cat, "Black");
    }

    @Test
    void nullStateValuesHaveConsistentEqualityAndHashing()
    {
        Cat first = new Cat(null, null);
        Cat second = new Cat(null, null);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        first.location = null;
        second.location = null;
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, new Cat("Black", "Park"));
    }

    @Test
    void locationsCompareTheirValueAndRejectOtherTypes()
    {
        Location park = new Location("Park");
        assertEquals(park, new Location("Park"));
        assertEquals(park.hashCode(), new Location("Park").hashCode());
        assertNotEquals(park, new Location("Garden"));
        assertNotEquals(park, "Park");
        assertEquals(new Location(null), new Location(null));
        assertEquals(new Location(null).hashCode(), new Location(null).hashCode());
    }
}
