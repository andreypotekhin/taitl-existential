package com.taitl.exlogic.unused.indexes;

import com.taitl.existential.indexes.*;
import com.taitl.existential.quantifiers.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ValueJoinTest
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

    static class Task
    {
        String team;
        final String title;

        Task(String team, String title)
        {
            this.team = team;
            this.title = title;
        }
    }

    ValueJoin<User, Task, String> join;

    @BeforeEach
    void setUp()
    {
        join = new ValueJoin<>(u -> u.team, t -> t.team);
    }

    @Test
    void testJoinAndLookup()
    {
        User alice = new User("A", "Alice");
        User bob = new User("B", "Bob");
        Task bug = new Task("A", "Fix bug");
        Task deploy = new Task("B", "Deploy");

        join.addLeft(alice);
        join.addLeft(bob);
        join.addRight(bug);
        join.addRight(deploy);

        assertEquals(alice, join.getLeft("A"));
        assertEquals(bob, join.getLeft("B"));
        assertEquals(bug, join.getRightByLeft(alice));
        assertEquals(alice, join.getLeftByRight(bug));
        assertEquals(deploy, join.getRight("B"));
    }

    @Test
    void testSingleValueReplacementPerKey()
    {
        User alice = new User("A", "Alice");
        User bob = new User("A", "Bob");
        Task bug = new Task("A", "Fix bug");
        join.addLeft(alice);
        join.addRight(bug);
        join.addLeft(bob);

        assertEquals(bob, join.getLeft("A"));
        assertNull(join.getRightByLeft(alice));
        assertEquals(bug, join.getRightByLeft(bob));
    }

    @Test
    void testReindexEitherSide()
    {
        User alice = new User("A", "Alice");
        Task bug = new Task("A", "Fix bug");
        join.addLeft(alice);
        join.addRight(bug);

        alice.team = "B";
        join.reindexLeft("A", "B", alice);

        assertNull(join.getLeft("A"));
        assertEquals(alice, join.getLeft("B"));

        bug.team = "B";
        join.reindexRight("A", "B", bug);

        assertNull(join.getRight("A"));
        assertEquals(bug, join.getRight("B"));
    }

    @Test
    void testReindexValidation()
    {
        User alice = new User("A", "Alice");
        join.addLeft(alice);
        IllegalArgumentException leftEx = assertThrows(IllegalArgumentException.class,
                () -> join.reindexLeft("A", "B", alice));
        assertTrue(leftEx.getMessage().contains("newKey"));

        Task bug = new Task("A", "Fix bug");
        join.addRight(bug);
        IllegalArgumentException rightEx = assertThrows(IllegalArgumentException.class,
                () -> join.reindexRight("A", "B", bug));
        assertTrue(rightEx.getMessage().contains("newKey"));
    }

    @Test
    void testIndexNullRemoves()
    {
        User alice = new User("A", "Alice");
        Task bug = new Task("A", "Fix bug");
        join.addLeft(alice);
        join.addRight(bug);

        join.indexLeft(alice, null);
        join.indexRight(bug, null);

        assertNull(join.getLeft("A"));
        assertNull(join.getRight("A"));
    }

    @Test
    void testJoinViewsAndExistsOnLeftView()
    {
        User alice = new User("A", "Alice");
        Task bug = new Task("A", "Fix bug");
        Task deploy = new Task("B", "Deploy");

        join.addLeft(alice);
        join.addRight(bug);
        join.addRight(deploy);

        assertEquals(bug, join.left().get(alice));
        assertEquals(alice, join.right().get(bug));
        assertNull(join.left().get(new User("A", "Ghost")));

        Exists<User> exists = new Exists<>(join.left());
        assertTrue(exists.test(alice));
        assertFalse(exists.test(new User("A", "Ghost")));
    }

    @Test
    void testGetKeysReturnNull()
    {
        ValueJoin<User, Task, String> nullLeft = new ValueJoin<>(u -> null, t -> t.team);
        assertThrows(IllegalArgumentException.class, () -> nullLeft.addLeft(new User("A", "Alice")));

        ValueJoin<User, Task, String> nullRight = new ValueJoin<>(u -> u.team, t -> null);
        assertThrows(IllegalArgumentException.class, () -> nullRight.addRight(new Task("A", "Fix bug")));
    }

    @Test
    void testAddAll()
    {
        User alice = new User("A", "Alice");
        User bob = new User("B", "Bob");
        Task bug = new Task("A", "Fix bug");
        Task deploy = new Task("B", "Deploy");

        join.addAllLeft(List.of(alice, bob));
        join.addAllRight(List.of(bug, deploy));

        assertEquals(alice, join.getLeft("A"));
        assertEquals(bob, join.getLeft("B"));
        assertEquals(bug, join.getRight("A"));
        assertEquals(deploy, join.getRight("B"));
        assertThrows(IllegalArgumentException.class, () -> join.addAllLeft(null));
        assertThrows(IllegalArgumentException.class, () -> join.addAllRight(null));
    }
}
