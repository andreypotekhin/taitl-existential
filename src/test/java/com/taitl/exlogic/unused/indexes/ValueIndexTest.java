package com.taitl.exlogic.unused.indexes;

import com.taitl.existential.indexes.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ValueIndexTest
{
    static class User
    {
        String team;
        final String name;

        User(String team, String name)
        {
            this.team = team;
            this.name = name;
        }
    }

    ValueIndex<String, User> usersByTeam;

    @BeforeEach
    void setUp()
    {
        usersByTeam = new ValueIndex<>(u -> u.team);
    }

    @Test
    void testConstructorAndMap()
    {
        assertTrue(usersByTeam instanceof Map);
        User alice = new User("A", "Alice");
        usersByTeam.add(alice);
        assertEquals(alice, usersByTeam.get("A"));
        assertTrue(usersByTeam.contains(alice));
        assertTrue(usersByTeam.containsKey("A"));
    }

    @Test
    void testRemoveAndMatching()
    {
        User alice = new User("A", "Alice");
        usersByTeam.put("A", alice);
        assertTrue(usersByTeam.remove("A", alice));

        usersByTeam.put("A", alice);
        assertEquals(alice, usersByTeam.removeMatching("A", u -> u.name.equals("Alice")));
        assertNull(usersByTeam.get("A"));
    }

    @Test
    void testReindexAndIndex()
    {
        User aliceV0 = new User("A", "Alice");
        User aliceV1 = new User("B", "Alice");

        usersByTeam.add(aliceV0);
        usersByTeam.index(aliceV0, aliceV1);
        assertNull(usersByTeam.get("A"));
        assertEquals(aliceV1, usersByTeam.get("B"));

        usersByTeam.index(aliceV1, null);
        assertNull(usersByTeam.get("B"));
    }

    @Test
    void testGetKeyReturnsNull()
    {
        ValueIndex<String, User> index = new ValueIndex<>(u -> null);
        assertThrows(IllegalArgumentException.class, () -> index.add(new User("A", "Alice")));
    }

    @Test
    void testAddAll()
    {
        User alice = new User("A", "Alice");
        User bob = new User("B", "Bob");
        usersByTeam.addAll(List.of(alice, bob));

        assertEquals(alice, usersByTeam.get("A"));
        assertEquals(bob, usersByTeam.get("B"));
        assertThrows(IllegalArgumentException.class, () -> usersByTeam.addAll(null));
    }
}
